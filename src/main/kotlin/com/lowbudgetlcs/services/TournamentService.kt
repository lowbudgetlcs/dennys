package com.lowbudgetlcs.services

import com.lowbudgetlcs.Dennys
import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.models.PostMatchCallback
import com.lowbudgetlcs.models.TeamId
import com.lowbudgetlcs.repositories.IGameRepository
import com.lowbudgetlcs.repositories.IMatchRepository
import com.lowbudgetlcs.repositories.IPlayerRepository
import com.lowbudgetlcs.repositories.ISeriesRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This service worker consumes [PostMatchCallback]s off of [queue] and saves
 * the result of the finished [Game]. It also checks if the [Series] owning
 * [Game] is complete. If it is, it saves the result.
 */
class TournamentService(
    private val gamesRepository: IGameRepository,
    private val seriesRepository: ISeriesRepository,
    private val playersRepository: IPlayerRepository,
    private val matchRepository: IMatchRepository,
    private val db: Dennys
) {

    private val logger: Logger = LoggerFactory.getLogger(TournamentService::class.java)

    /**
     * Fetches a match from the RiotAPI and process the winning and losing teams. Then, saves
     * the winner, loser, and result of the game. If the series owning said game is complete,
     * save the winner and loser of the series.
     */
    suspend fun process(callback: PostMatchCallback) {
        logger.info("🔍 Fetching match details for game ID: ${callback.gameId}...")
        val tournamentMatch = matchRepository.getMatch(callback.gameId)
        tournamentMatch?.let { match ->
            try {
                db.transaction {
                    val (winner, loser) = match.matchInfo.participants.partition { it.win }.let { (winners, losers) ->
                        playersRepository.fetchTeamId(winners) to playersRepository.fetchTeamId(losers)
                    }

                    if (winner == null || loser == null) throw IllegalArgumentException("TeamId not found.")

                    logger.debug("\uD83C\uDFC6 Winner: {}, ❌ Loser: {}", winner, loser)

                    updateGame(callback, winner, loser)?.let {
                        updateSeries(
                            it, winner, loser
                        )
                    }
                }
            } catch (e: IllegalArgumentException) {
                logger.warn("⚠️ Could not fetch one or both TeamIds.")
            }
            logger.info("✅ Finished processing match `${match.matchInfo.gameId}` for shortcode `${callback.shortCode}`")
        }
    }

    /**
     * Updates the game in storage derived from [callback] with a [winner] and [loser].
     */
    private fun updateGame(callback: PostMatchCallback, winner: TeamId, loser: TeamId): Game? {
        logger.info("📝 Updating game record for shortcode: ${callback.shortCode}...")

        gamesRepository.get(shortcode = callback.shortCode)?.let { game ->
            val updatedGame = game.copy(winner = winner, loser = loser, callbackResult = callback)
            return gamesRepository.update(updatedGame)
        }
        return null
    }

    /**
     * Updates the series in storage that owns [game] with a winner and loser.
     */
    private fun updateSeries(game: Game, team1: TeamId, team2: TeamId) {
        logger.info("📝 Updating series record for derived from: ${game.id}...")
        seriesRepository.get(id = game.series)?.let { series ->
            logger.debug("\uD83D\uDCDD Updating series record: {}...", series.id)
            // Magic number yayyyy! This needs an actual solution- for now the app only supports Bo3.
            val winCondition = 2
            val team1Wins = gamesRepository.get(team = team1, series = game.series).size
            val team2Wins = gamesRepository.get(team = team2, series = game.series).size

            fun logWin(team: TeamId) = logger.debug("\uD83C\uDFC5 Team {} wins series {}!", team, series.id)
            when (winCondition) {
                team1Wins -> {
                    seriesRepository.update(series.copy(winner = team1, loser = team2))
                    logWin(team1)
                }

                team2Wins -> {
                    seriesRepository.update(series.copy(loser = team1, winner = team2))
                    logWin(team2)
                }
                // Otherwise, series has not concluded
                else -> {
                    logger.debug("⏳ Series {} is still ongoing...", series.id)
                }
            }
        }
    }
}
