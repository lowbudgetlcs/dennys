package com.lowbudgetlcs.domain.stats

import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.stats.models.ChampionAggregate
import com.lowbudgetlcs.domain.stats.models.GameStats
import com.lowbudgetlcs.domain.stats.models.PlayerAggregate
import com.lowbudgetlcs.domain.stats.models.PlayerGameStats
import com.lowbudgetlcs.domain.stats.models.StatsQuery
import com.lowbudgetlcs.domain.stats.models.StatsSort
import com.lowbudgetlcs.domain.stats.models.TeamAggregate
import com.lowbudgetlcs.domain.stats.models.TeamGameStats
import com.lowbudgetlcs.domain.stats.models.TeamRecord
import com.lowbudgetlcs.repositories.riotmatchdata.IRiotMatchDataRepository
import com.lowbudgetlcs.repositories.stats.IStatsAggregateRepository
import com.lowbudgetlcs.repositories.stats.IStatsRepository
import kotlinx.serialization.json.JsonObject

private const val MAX_LIMIT = 1000

class StatsService(
    private val riotMatchDataRepo: IRiotMatchDataRepository,
    private val statsRepo: IStatsRepository,
    private val aggregateRepo: IStatsAggregateRepository,
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

    override fun getPlayerGames(
        query: StatsQuery,
        limit: Int,
        offset: Int,
    ): List<PlayerGameStats> {
        requirePage(limit, offset)
        return statsRepo.getPlayerGames(query, limit, offset)
    }

    override fun getTeamGames(
        query: StatsQuery,
        limit: Int,
        offset: Int,
    ): List<TeamGameStats> {
        requirePage(limit, offset)
        return statsRepo.getTeamGames(query, limit, offset)
    }

    override fun getPlayerAggregates(
        query: StatsQuery,
        sort: StatsSort?,
        limit: Int,
        offset: Int,
        minGames: Int?,
    ): List<PlayerAggregate> {
        requirePage(limit, offset)
        return aggregateRepo.getPlayerAggregates(query, sort ?: StatsSort("games"), limit, offset, minGames)
    }

    override fun getTeamAggregates(
        query: StatsQuery,
        sort: StatsSort?,
        limit: Int,
        offset: Int,
        minGames: Int?,
    ): List<TeamAggregate> {
        requirePage(limit, offset)
        return aggregateRepo.getTeamAggregates(query, sort ?: StatsSort("games"), limit, offset, minGames)
    }

    override fun getTeamRecords(
        query: StatsQuery,
        limit: Int,
        offset: Int,
    ): List<TeamRecord> {
        requirePage(limit, offset)
        return aggregateRepo.getTeamRecords(query, limit, offset)
    }

    override fun getChampionAggregates(
        query: StatsQuery,
        sort: StatsSort?,
        limit: Int,
        offset: Int,
        minGames: Int?,
    ): List<ChampionAggregate> {
        requirePage(limit, offset)
        return aggregateRepo.getChampionAggregates(query, sort ?: StatsSort("picks"), limit, offset, minGames)
    }

    private fun requirePage(
        limit: Int,
        offset: Int,
    ) {
        require(limit in 1..MAX_LIMIT) { "limit must be between 1 and $MAX_LIMIT." }
        require(offset >= 0) { "offset must not be negative." }
    }
}
