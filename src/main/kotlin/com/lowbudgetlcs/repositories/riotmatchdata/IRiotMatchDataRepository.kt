package com.lowbudgetlcs.repositories.riotmatchdata

import com.lowbudgetlcs.domain.series.game.models.RiotMatchData
import com.lowbudgetlcs.domain.series.game.models.types.GameId
import kotlinx.serialization.json.JsonObject

interface IRiotMatchDataRepository {
    /** Inserts, or replaces the stored payloads for a game that already has Riot match data. */
    fun save(
        gameId: GameId,
        match: JsonObject,
        timeline: JsonObject?,
    ): RiotMatchData?

    fun getByGameId(gameId: GameId): RiotMatchData?
}
