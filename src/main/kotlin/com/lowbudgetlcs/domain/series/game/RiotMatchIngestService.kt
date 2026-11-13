package com.lowbudgetlcs.domain.series.game

import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.gateways.riot.match.IRiotMatchGateway
import com.lowbudgetlcs.repositories.game.IGameRepository
import com.lowbudgetlcs.repositories.riotmatchdata.IRiotMatchDataRepository
import kotlinx.coroutines.CancellationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/** Two Riot calls per game */
private const val INGEST_BATCH = 50

class RiotMatchIngestService(
    private val gameRepo: IGameRepository,
    private val riotMatchGateway: IRiotMatchGateway,
    private val riotMatchDataRepo: IRiotMatchDataRepository,
) : IRiotMatchIngestService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    // shortcut: a game Riot never returns (deleted, or past retention) is retried every run;
    // add an attempts column if those fill the batch.
    override suspend fun ingestPending() {
        gameRepo.getMissingRiotMatchData(INGEST_BATCH).forEach { game ->
            try {
                ingest(game)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logger.warn("Could not ingest Riot match data for game '${game.id.value}': ${e.message}")
            }
        }
    }

    private suspend fun ingest(game: Game) {
        val matchId = game.riotMatchId ?: return
        val match = riotMatchGateway.getMatch(matchId)
        val timeline =
            try {
                riotMatchGateway.getTimeline(matchId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logger.warn("Storing game '${game.id.value}' without a timeline: ${e.message}")
                null
            }
        riotMatchDataRepo.save(game.id, match, timeline)
    }
}
