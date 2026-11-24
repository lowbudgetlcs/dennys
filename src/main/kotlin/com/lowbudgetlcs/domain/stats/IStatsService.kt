package com.lowbudgetlcs.domain.stats

import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.stats.models.GameStats
import kotlinx.serialization.json.JsonObject

/** Each lookup throws [NoSuchElementException] for an unknown game or one without that data stored yet. */
interface IStatsService {
    fun getMatch(gameId: GameId): JsonObject

    fun getTimeline(gameId: GameId): JsonObject

    fun getGameStats(gameId: GameId): GameStats
}
