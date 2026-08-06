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
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.RefreshOutcome
import com.lowbudgetlcs.domain.series.models.ReportOutcome
import com.lowbudgetlcs.domain.series.models.ReportedResult
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.SeriesResult
import com.lowbudgetlcs.domain.series.models.SeriesWithGames
import com.lowbudgetlcs.domain.series.models.toSeriesWithGames
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.types.TeamId
import com.lowbudgetlcs.equalsIgnoreOrder
import com.lowbudgetlcs.gateways.GatewayException
import com.lowbudgetlcs.gateways.riot.RiotApiException
import com.lowbudgetlcs.gateways.riot.tournament.IRiotTournamentGateway
import com.lowbudgetlcs.gateways.riot.tournament.RiotRegion
import com.lowbudgetlcs.gateways.riot.tournament.RiotTournamentGamesV5Dto
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.event.IEventRepository
import com.lowbudgetlcs.repositories.game.IGameRepository
import com.lowbudgetlcs.repositories.series.ISeriesRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository
import com.lowbudgetlcs.repositories.tournamentcode.ITournamentCodeRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant

class SeriesService(
    private val codeRepo: ITournamentCodeRepository,
    private val seriesRepo: ISeriesRepository,
    private val eventRepo: IEventRepository,
    private val teamRepo: ITeamRepository,
    private val gameRepo: IGameRepository,
    private val gate: IRiotTournamentGateway,
) : ISeriesService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

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
        if (series.completed || series.reopenedAt != null) {
            logger.debug("Series '$id' is not eligible for auto-close, skipping evaluation.")
            return series
        }
        val wins =
            gameRepo
                .getBySeriesId(id)
                .mapNotNull { it.result }
                .groupingBy { it.winningTeamId }
                .eachCount()
        val leader = wins.maxByOrNull { it.value } ?: return series
        // Wins, not games played: a 2-0 Bo3 is over after two games.
        if (leader.value <= series.totalGames / 2) {
            logger.debug("Series '$id' at ${leader.value} win(s) of ${series.totalGames}, still open.")
            return series
        }
        val loser = series.participants.toList().firstOrNull { it != leader.key } ?: return series
        logger.debug("Series '$id' won by '${leader.key}', closing.")
        return seriesRepo.complete(id, Instant.now(), SeriesResult(leader.key, loser))
            ?: throw DatabaseException("Failed to complete series with id '${id.value}'.")
    }

    override suspend fun refreshFromRiot(id: SeriesId): RefreshOutcome {
        val series = getSeries(id)
        val recordedCodeIds = gameRepo.getBySeriesId(id).mapNotNull { it.tournamentCodeId }.toSet()
        val outstanding = codeRepo.getBySeriesId(id).filter { it.id !in recordedCodeIds }
        if (outstanding.isEmpty()) {
            logger.debug("Series '$id' has no outstanding tournament codes.")
            return RefreshOutcome.ANSWERED_EMPTY
        }

        var attributed = false
        val unverified = mutableListOf<Shortcode>()
        for (code in outstanding) {
            val riotGames =
                try {
                    gate.getGames(code.shortcode)
                } catch (e: RiotApiException) {
                    logger.warn("Could not reach Riot for shortcode '${code.shortcode.value}': ${e.message}")
                    unverified += code.shortcode
                    continue
                }
            riotGames.forEach { riotGame ->
                val result = resolveWinner(series, riotGame)
                if (result != null) {
                    gameRepo.insert(
                        NewGame(
                            seriesId = id,
                            tournamentCodeId = code.id,
                            riotMatchId = riotMatchIdOf(riotGame),
                            result = result,
                        ),
                    )
                    attributed = true
                }
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

    override suspend fun reportResult(
        id: SeriesId,
        report: ReportedResult,
    ): ReportOutcome {
        val series = getSeries(id)
        require(report.tournamentCodeId == null || report.shortcode == null) {
            "Provide either tournamentCodeId or shortcode, not both."
        }
        val declared = declaredResult(series, report)
        val target = resolveTarget(series, report)

        val before = gameRepo.getBySeriesId(id)
        if (target != null) {
            before.firstOrNull { it.tournamentCodeId == target.id && it.result != null }?.let {
                logger.info("Code '${target.shortcode.value}' already has a result, returning game '${it.id.value}'.")
                return ReportOutcome(it, recorded = false)
            }
        }
        val outstanding = codeRepo.getBySeriesId(id).filter { code -> before.none { it.tournamentCodeId == code.id } }

        refreshQuietly(id)
        val pulled =
            gameRepo.getBySeriesId(id).filter { game ->
                game.result != null && before.none { it.id == game.id }
            }

        if (target != null) {
            pulled.firstOrNull { it.tournamentCodeId == target.id }?.let { return ReportOutcome(it, recorded = true) }
            return ReportOutcome(record(id, target.id, declared ?: noWinner(id)), recorded = true)
        }
        pulled.firstOrNull()?.let { return ReportOutcome(it, recorded = true) }

        if (declared == null && outstanding.isEmpty()) {
            before.lastOrNull { it.result != null }?.let {
                logger.info("Series '$id' has no outstanding codes, returning recorded game '${it.id.value}'.")
                return ReportOutcome(it, recorded = false)
            }
        }
        return ReportOutcome(record(id, null, declared ?: noWinner(id)), recorded = true)
    }

    private fun noWinner(id: SeriesId): Nothing =
        throw IllegalArgumentException(
            "No winner was named and Riot has no record of a game for series '${id.value}'.",
        )

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

    override fun completeSeries(
        id: SeriesId,
        winningTeamId: TeamId?,
        losingTeamId: TeamId?,
    ): Series {
        val series = getSeries(id)
        check(!series.completed) {
            "Series '${id.value}' is already complete. Reopen it before completing it again."
        }
        val result = winnerAndLoser(series, winningTeamId, losingTeamId)?.let { SeriesResult(it.first, it.second) }
        logger.info("Closing series '$id' manually, winner '${result?.winningTeamId}'.")
        return seriesRepo.complete(id, Instant.now(), result)
            ?: throw DatabaseException("Failed to complete series with id '${id.value}'.")
    }

    override fun reopenSeries(id: SeriesId): Series {
        val series = getSeries(id)
        check(series.completed) { "Series '${id.value}' is not complete, so it cannot be reopened." }
        logger.info("Reopening series '$id'.")
        return seriesRepo.reopen(id, Instant.now())
            ?: throw DatabaseException("Failed to reopen series with id '${id.value}'.")
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

    private fun resolveTarget(
        series: Series,
        report: ReportedResult,
    ): TournamentCode? {
        val code =
            report.tournamentCodeId?.let {
                codeRepo.getById(it) ?: throw NoSuchElementException("Tournament code with id ${it.value} not found")
            } ?: report.shortcode?.let {
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

    private fun seriesMetadata(id: SeriesId): String = """{"seriesId":${id.value}}"""

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

    override fun removeSeries(id: SeriesId) {
        logger.debug("Deleting series '$id'...")
        try {
            return seriesRepo.delete(id)
        } catch (e: Throwable) {
            throw DatabaseException("Failed to remove series")
        }
    }

    override suspend fun createGame(newCode: NewTournamentCode): TournamentCode {
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
        refreshQuietly(series.id)
        logger.debug("Fetching tournament id for event '${series.eventId}'...t add")
        val event =
            eventRepo.getById(series.eventId)
                ?: throw DatabaseException("Series with id '${series.id}' does not have parent event.")
        val response =
            gate.getCode(event.riotTournamentId, ShortcodeOptions(metadata = seriesMetadata(series.id)))
                ?: throw GatewayException("Failed to create tournament code.")
        val shortcode = response.codes.first()
        return codeRepo.insert(newCode, shortcode.toShortcode()) ?: throw DatabaseException("Failed to save game.")
    }
}
