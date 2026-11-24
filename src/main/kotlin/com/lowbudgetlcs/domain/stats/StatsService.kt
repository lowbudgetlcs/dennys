package com.lowbudgetlcs.domain.stats

import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.stats.models.GameStats
import com.lowbudgetlcs.repositories.riotmatchdata.IRiotMatchDataRepository
import com.lowbudgetlcs.repositories.stats.IStatsRepository
import kotlinx.serialization.json.JsonObject

class StatsService(
    private val riotMatchDataRepo: IRiotMatchDataRepository,
    private val statsRepo: IStatsRepository,
) : IStatsService {
    override fun getMatch(gameId: GameId): JsonObject =
        riotMatchDataRepo.getByGameId(gameId)?.match
            ?: throw NoSuchElementException("Game '${gameId.value}' has no Riot match data.")

    override fun getTimeline(gameId: GameId): JsonObject =
        riotMatchDataRepo.getByGameId(gameId)?.timeline
            ?: throw NoSuchElementException("Game '${gameId.value}' has no Riot timeline.")

    override fun getGameStats(gameId: GameId): GameStats {
        val stats = statsRepo.getGameStats(gameId)
        if (stats.players.isEmpty() && stats.teams.isEmpty()) {
            throw NoSuchElementException("Game '${gameId.value}' has no Riot match data.")
        }
        return stats
    }
}
