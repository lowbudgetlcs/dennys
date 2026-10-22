package com.lowbudgetlcs.domain.series

import com.lowbudgetlcs.domain.account.models.types.Puuid
import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.event.models.ShortcodeOptions
import com.lowbudgetlcs.domain.event.models.toShortcode
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.NewGame
import com.lowbudgetlcs.domain.series.game.models.NewTournamentCode
import com.lowbudgetlcs.domain.series.game.models.TournamentCode
import com.lowbudgetlcs.domain.series.game.models.toRiotMatchId
import com.lowbudgetlcs.domain.series.game.models.types.RiotMatchId
import com.lowbudgetlcs.domain.series.game.models.types.TournamentCodeId
import com.lowbudgetlcs.domain.series.models.IssueOutcome
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.RefreshOutcome
import com.lowbudgetlcs.domain.series.models.ReportOutcome
import com.lowbudgetlcs.domain.series.models.ReportedResult
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.SeriesResult
import com.lowbudgetlcs.domain.series.models.SeriesWithGames
import com.lowbudgetlcs.domain.series.models.toSeriesWithGames
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.series.models.types.SeriesStatus
import com.lowbudgetlcs.domain.team.models.types.TeamId
import com.lowbudgetlcs.equalsIgnoreOrder
import com.lowbudgetlcs.gateways.GatewayException
import com.lowbudgetlcs.gateways.riot.RiotApiException
import com.lowbudgetlcs.gateways.riot.tournament.IRiotTournamentGateway
import com.lowbudgetlcs.gateways.riot.tournament.RiotRegion
import com.lowbudgetlcs.gateways.riot.tournament.RiotTournamentGamesV5Dto
import com.lowbudgetlcs.repositories.ConflictException
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.event.IEventRepository
import com.lowbudgetlcs.repositories.game.IGameRepository
import com.lowbudgetlcs.repositories.series.ISeriesRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository
import com.lowbudgetlcs.repositories.tournamentcode.ITournamentCodeRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

private const val RELAY_METADATA_TAG = "LBLCS"

/** Tournament codes a series may be issued for one game before a result has to be recorded. */
const val DEFAULT_MAX_CODES_PER_GAME = 2

@Suppress("LongParameterList")
class SeriesService(
    private val codeRepo: ITournamentCodeRepository,
    private val seriesRepo: ISeriesRepository,
    private val eventRepo: IEventRepository,
    private val teamRepo: ITeamRepository,
    private val gameRepo: IGameRepository,
    private val gate: IRiotTournamentGateway,
    private val maxCodesPerGame: Int = DEFAULT_MAX_CODES_PER_GAME,
) : ISeriesService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    // shortcut: in-process lock, Denny's runs as one container; move to a DB lock if it ever scales out.
    private val issuing = ConcurrentHashMap<SeriesId, Mutex>()

    override fun createSeries(series: NewSeries): Series {
        logger.debug("Creating new series...")
        logger.debug(series.toString())
        require(series.totalGames > 0) { "A series must contain at least 1 game." }
        val validate = { id: TeamId ->
            logger.debug("Validating team '$id' exists and is participating in event '${series.eventId}'...")
            val team = teamRepo.getById(id) ?: throw NoSuchElementException("Team with id $id not found")
            check(team.eventId == series.eventId) { "Team with id $id is not part of the event" }
        }
        validate(series.participantIds.first)
        validate(series.participantIds.second)

        return seriesRepo.insert(series) ?: throw DatabaseException("Failed to create series")
    }

    override fun getAllSeriesFromEvent(id: EventId): List<Series> {
        logger.debug("Fetching all series in event '$id'...")
        return seriesRepo.getAllByEventId(id)
    }

    override fun getSeries(id: SeriesId): Series {
        logger.debug("Fetching series '$id'...")
        return seriesRepo.getById(id) ?: throw NoSuchElementException("Series not found")
    }

    override fun getSeriesWithGames(id: SeriesId): SeriesWithGames {
        logger.debug("Fetching series '$id' with codes and games...")
        val series = getSeries(id)
        return series.toSeriesWithGames(codeRepo.getBySeriesId(id), gameRepo.getBySeriesId(id))
    }

    override fun evaluateCompletion(id: SeriesId): Series {
        val series = getSeries(id)
        if (series.status != SeriesStatus.NOT_STARTED && series.status != SeriesStatus.IN_PROGRESS) {
            logger.debug("Series '$id' is not eligible for auto-close, skipping evaluation.")
            return series
        }
        if (series.status == SeriesStatus.NOT_STARTED) seriesRepo.markInProgress(id)
        val open = series.copy(status = SeriesStatus.IN_PROGRESS)
        val wins =
            gameRepo
                .getBySeriesId(id)
                .mapNotNull { it.result }
                .groupingBy { it.winningTeamId }
                .eachCount()
        val leader = wins.maxByOrNull { it.value } ?: return open
        // Wins, not games played: a 2-0 Bo3 is over after two games.
        if (leader.value <= series.totalGames / 2) {
            logger.debug("Series '$id' at ${leader.value} win(s) of ${series.totalGames}, still open.")
            return open
        }
        val loser = series.participants.toList().firstOrNull { it != leader.key } ?: return open
        logger.debug("Series '$id' won by '${leader.key}', closing.")
        return seriesRepo.setStatus(id, SeriesStatus.COMPLETE, SeriesResult(leader.key, loser))
            ?: throw DatabaseException("Failed to complete series with id '${id.value}'.")
    }

    override suspend fun refreshFromRiot(id: SeriesId): RefreshOutcome {
        val series = getSeries(id)
        val recorded = gameRepo.getBySeriesId(id)
        val recordedCodeIds = recorded.mapNotNull { it.tournamentCodeId }.toSet()
        val recordedMatchIds = recorded.mapNotNull { it.riotMatchId }.toSet()
        val outstanding = codeRepo.getBySeriesId(id).filter { it.id !in recordedCodeIds }
        if (outstanding.isEmpty()) {
            logger.debug("Series '$id' has no outstanding tournament codes.")
            return RefreshOutcome.ANSWERED_EMPTY
        }

        var attributed = false
        val unverified = mutableListOf<Shortcode>()
        for (code in outstanding) {
            when (refreshCode(series, code, recordedMatchIds)) {
                RefreshOutcome.ATTRIBUTED -> attributed = true
                RefreshOutcome.UNREACHABLE -> unverified += code.shortcode
                RefreshOutcome.ANSWERED_EMPTY -> Unit
            }
        }

        if (attributed) evaluateCompletion(id)
        return when {
            attributed -> RefreshOutcome.ATTRIBUTED
            unverified.isNotEmpty() -> {
                logger.warn(
                    "Series '$id' could not be verified against Riot for codes " +
                        unverified.joinToString { it.value },
                )
                RefreshOutcome.UNREACHABLE
            }

            else -> RefreshOutcome.ANSWERED_EMPTY
        }
    }

    /**
     * Ask Riot about one tournament code and record whatever it returns.
     *
     * Shared by the bulk refresh, the Riot callback and the targeted refresh endpoint so all three
     * attribute games identically. Does not evaluate completion — the caller does that once, after
     * however many codes it walked.
     *
     * @param recordedMatchIds matches already stored for this series, so a match Riot reports under
     *   a second code is not recorded twice.
     */
    private suspend fun refreshCode(
        series: Series,
        code: TournamentCode,
        recordedMatchIds: Set<RiotMatchId>,
    ): RefreshOutcome {
        val riotGames =
            try {
                gate.getGames(code.shortcode)
            } catch (e: RiotApiException) {
                logger.warn("Could not reach Riot for shortcode '${code.shortcode.value}': ${e.message}")
                return RefreshOutcome.UNREACHABLE
            }
        var attributed = false
        riotGames.forEach { riotGame ->
            val matchId = riotMatchIdOf(riotGame)
            if (matchId != null && matchId in recordedMatchIds) {
                logger.warn("Riot match '${matchId.value}' is already recorded for series '${series.id}', skipping.")
                return@forEach
            }
            val result = resolveWinner(series, riotGame)
            if (result != null) {
                gameRepo.insert(
                    NewGame(
                        seriesId = series.id,
                        tournamentCodeId = code.id,
                        riotMatchId = matchId,
                        result = result,
                    ),
                )
                attributed = true
            }
        }
        return if (attributed) RefreshOutcome.ATTRIBUTED else RefreshOutcome.ANSWERED_EMPTY
    }

    override suspend fun refreshFromCode(
        id: SeriesId,
        tournamentCodeId: TournamentCodeId?,
        shortcode: Shortcode?,
    ): RefreshOutcome {
        require((tournamentCodeId == null) != (shortcode == null)) {
            "Provide exactly one of tournamentCodeId or shortcode."
        }
        val series = getSeries(id)
        val code =
            resolveTarget(series, tournamentCodeId, shortcode)
                ?: throw NoSuchElementException("No tournament code was identified for series '${id.value}'.")

        val recorded = gameRepo.getBySeriesId(id)
        // A code that already has a game is left alone. Re-asking Riot would insert a second row for
        // the same match whenever the existing game was self-reported, because such a game carries no
        // riotMatchId for the dedupe below to match on.
        if (recorded.any { it.tournamentCodeId == code.id }) {
            logger.info("Code '${code.shortcode.value}' already has a game recorded, nothing to refresh.")
            return RefreshOutcome.ANSWERED_EMPTY
        }

        val outcome = refreshCode(series, code, recorded.mapNotNull { it.riotMatchId }.toSet())
        if (outcome == RefreshOutcome.ATTRIBUTED) evaluateCompletion(id)
        return outcome
    }

    override suspend fun reportResult(
        id: SeriesId,
        report: ReportedResult,
    ): ReportOutcome {
        val series = getSeries(id)
        require(report.tournamentCodeId == null || report.shortcode == null) {
            "Provide either tournamentCodeId or shortcode, not both."
        }
        val declared = declaredResult(series, report) ?: throw IllegalArgumentException("winnerTeamId is required.")
        val target =
            resolveTarget(series, report.tournamentCodeId, report.shortcode)
                ?: return ReportOutcome(record(id, null, declared), recorded = true)

        existingGame(id, target)?.let {
            logger.info("Code '${target.shortcode.value}' already has a result, returning game '${it.id.value}'.")
            return ReportOutcome(it, recorded = false)
        }
        return try {
            ReportOutcome(record(id, target.id, declared), recorded = true)
        } catch (e: IllegalStateException) {
            // A concurrent report for this code won the insert.
            ReportOutcome(existingGame(id, target) ?: throw e, recorded = false)
        }
    }

    private fun existingGame(
        id: SeriesId,
        code: TournamentCode,
    ): Game? = gameRepo.getBySeriesId(id).firstOrNull { it.tournamentCodeId == code.id && it.result != null }

    private fun record(
        id: SeriesId,
        codeId: TournamentCodeId?,
        result: GameResult,
    ): Game {
        val game =
            gameRepo.insert(NewGame(id, codeId, null, result))
                ?: throw DatabaseException("Failed to record a result for series '${id.value}'.")
        evaluateCompletion(id)
        return game
    }

    override fun patchSeries(
        id: SeriesId,
        status: SeriesStatus,
        winningTeamId: TeamId?,
        doubleLoss: Boolean,
    ): Series {
        require(winningTeamId == null || !doubleLoss) { "A double-loss series cannot have a winner." }
        require(status == SeriesStatus.COMPLETE || (winningTeamId == null && !doubleLoss)) {
            "Only a COMPLETE series can have a winner or a double loss."
        }
        val series = getSeries(id)
        val result = winnerAndLoser(series, winningTeamId, null)?.let { SeriesResult(it.first, it.second) }
        logger.info("Setting series '$id' to $status, winner '${result?.winningTeamId}', double loss $doubleLoss.")
        return seriesRepo.setStatus(id, status, result, doubleLoss)
            ?: throw DatabaseException("Failed to update series with id '${id.value}'.")
    }

    private fun declaredResult(
        series: Series,
        report: ReportedResult,
    ): GameResult? =
        winnerAndLoser(series, report.winningTeamId, report.losingTeamId)
            ?.let { GameResult(it.first, it.second) }

    private fun winnerAndLoser(
        series: Series,
        winningTeamId: TeamId?,
        losingTeamId: TeamId?,
    ): Pair<TeamId, TeamId>? {
        if (winningTeamId == null) {
            require(losingTeamId == null) { "A losing team cannot be given without a winning team." }
            return null
        }
        val (first, second) = series.participants
        require(winningTeamId == first || winningTeamId == second) {
            "Team '${winningTeamId.value}' is not a participant in series '${series.id.value}'."
        }
        val loser = if (winningTeamId == first) second else first
        losingTeamId?.let {
            require(it == loser) { "Team '${it.value}' is not the losing participant in series '${series.id.value}'." }
        }
        return winningTeamId to loser
    }

    /**
     * Resolve a code by id or by shortcode, whichever was supplied, and refuse one that belongs to a
     * different series. Returns null when neither was supplied — which `reportResult` records as a
     * codeless game, and `refreshFromCode` rejects up front.
     */
    private fun resolveTarget(
        series: Series,
        tournamentCodeId: TournamentCodeId?,
        shortcode: Shortcode?,
    ): TournamentCode? {
        val code =
            tournamentCodeId?.let {
                codeRepo.getById(it) ?: throw NoSuchElementException("Tournament code with id ${it.value} not found")
            } ?: shortcode?.let {
                codeRepo.getByShortcode(it) ?: throw NoSuchElementException("Tournament code '${it.value}' not found")
            } ?: return null
        if (code.seriesId != series.id) {
            throw NoSuchElementException(
                "Tournament code '${code.shortcode.value}' does not belong to series '${series.id.value}'",
            )
        }
        return code
    }

    private suspend fun refreshQuietly(id: SeriesId) {
        try {
            refreshFromRiot(id)
        } catch (e: Throwable) {
            logger.warn("Could not refresh series '$id' from Riot: ${e.message}")
        }
    }

    override suspend fun refreshFromShortcode(shortcode: Shortcode): RefreshOutcome? {
        val code = codeRepo.getByShortcode(shortcode)
        if (code == null) {
            logger.warn("Callback for unknown shortcode '${shortcode.value}', ignoring.")
            return null
        }
        return refreshFromRiot(code.seriesId)
    }

    private fun seriesMetadata(id: SeriesId): String = """{"tag":"$RELAY_METADATA_TAG","seriesId":${id.value}}"""

    /**
     * Codes issued since the most recent recorded game, coded or codeless. Recording a result ends
     * the current game, so the allowance resets rather than accumulating across a long series.
     */
    private fun codesForCurrentGame(id: SeriesId): List<TournamentCode> {
        val since = gameRepo.getBySeriesId(id).maxOfOrNull { it.createdAt }
        return codeRepo.getBySeriesId(id).filter { since == null || it.createdAt > since }
    }

    private fun riotMatchIdOf(riotGame: RiotTournamentGamesV5Dto): RiotMatchId? {
        val platformId = RiotRegion.platformIdOf(riotGame.region)
        if (platformId == null) {
            logger.warn("Unknown Riot region '${riotGame.region}' on shortcode '${riotGame.shortCode}'.")
            return null
        }
        return "${platformId}_${riotGame.gameId}".toRiotMatchId()
    }

    private fun resolveWinner(
        series: Series,
        riotGame: RiotTournamentGamesV5Dto,
    ): GameResult? {
        val puuids = riotGame.winningTeam.mapNotNull { runCatching { Puuid(it.puuid) }.getOrNull() }
        val resolved = teamRepo.getTeamIdsByPuuids(puuids)
        if (resolved.size < puuids.size) {
            logger.warn(
                "Series '${series.id}' shortcode '${riotGame.shortCode}': " +
                    "${puuids.size - resolved.size} of ${puuids.size} winning puuids are unregistered.",
            )
        }
        val counts = resolved.groupingBy { it }.eachCount()
        val (first, second) = series.participants
        val winner = listOf(first, second).maxByOrNull { counts[it] ?: 0 }
        if (winner == null || (counts[winner] ?: 0) == 0) {
            logger.warn(
                "Series '${series.id}' shortcode '${riotGame.shortCode}': " +
                    "no winning puuid resolved to either roster, leaving it for self-serve reporting.",
            )
            return null
        }
        return GameResult(winner, if (winner == first) second else first)
    }

    override fun removeSeries(
        eventId: EventId,
        id: SeriesId,
    ) {
        logger.debug("Deleting series '$id' from event '$eventId'...")
        val series = getSeries(id)
        if (series.eventId != eventId) {
            throw NoSuchElementException("Series '${id.value}' is not part of event '${eventId.value}'.")
        }
        val codes = codeRepo.getBySeriesId(id).size
        val games = gameRepo.getBySeriesId(id).size
        check(codes == 0 && games == 0) {
            "Series '${id.value}' has $codes tournament code(s) and $games game(s) recorded against it and " +
                "cannot be deleted. Complete the series instead."
        }
        try {
            return seriesRepo.delete(id)
        } catch (e: Throwable) {
            throw DatabaseException("Failed to remove series")
        }
    }

    override suspend fun createGame(
        newCode: NewTournamentCode,
        forceNew: Boolean,
    ): IssueOutcome {
        logger.debug("Creating new game...")
        logger.debug(newCode.toString())
        val series = getSeries(newCode.seriesId) // Throws if not found
        val blueTeam =
            teamRepo.getById(newCode.blueTeamId)
                ?: throw NoSuchElementException("Team with id ${newCode.blueTeamId.value} not found")
        val redTeam =
            teamRepo.getById(newCode.redTeamId)
                ?: throw NoSuchElementException("Team with id ${newCode.redTeamId.value} not found")
        require(
            (redTeam.id to blueTeam.id).equalsIgnoreOrder(series.participants),
        ) {
            "Provided teams are not part of series with id ${series.id.value}."
        }
        return issuing.computeIfAbsent(series.id) { Mutex() }.withLock {
            refreshQuietly(series.id)
            val current = codesForCurrentGame(series.id)
            if (!forceNew) {
                current.maxByOrNull { it.createdAt }?.let { return@withLock IssueOutcome(it, issued = false) }
            }
            if (current.size >= maxCodesPerGame) {
                logger.warn(
                    "Series '${series.id}' has ${current.size} code(s) for the current game, refusing to issue another.",
                )
                throw ConflictException(
                    "Series '${series.id.value}' has already been issued ${current.size} tournament code(s) for this " +
                        "game. Report the result of the game that was played, or play a custom game and report the " +
                        "winner.",
                    reason = "CODE_LIMIT",
                )
            }
            val event =
                eventRepo.getById(series.eventId)
                    ?: throw DatabaseException("Series with id '${series.id}' does not have parent event.")
            val response = gate.getCode(event.riotTournamentId, ShortcodeOptions(metadata = seriesMetadata(series.id)))
            val shortcode =
                response.codes.firstOrNull()
                    ?: throw GatewayException("Riot returned no tournament codes for event '${event.id.value}'.")
            val code =
                codeRepo.insert(newCode, shortcode.toShortcode()) ?: throw DatabaseException("Failed to save game.")
            seriesRepo.markInProgress(series.id)
            IssueOutcome(code, issued = true)
        }
    }
}
