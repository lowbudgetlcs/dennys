package com.lowbudgetlcs.repositories.riotmatchdata

import com.lowbudgetlcs.domain.series.game.models.RiotMatchData
import com.lowbudgetlcs.domain.series.game.models.toGameId
import com.lowbudgetlcs.domain.series.game.models.types.GameId
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.jooq.DSLContext
import org.jooq.JSONB
import org.jooq.impl.DSL.currentInstant
import org.jooq.storage.tables.records.RiotMatchDataRecord
import org.jooq.storage.tables.references.RIOT_MATCH_DATA

class RiotMatchDataRepository(
    private val dsl: DSLContext,
) : IRiotMatchDataRepository {
    override fun save(
        gameId: GameId,
        match: JsonObject,
        timeline: JsonObject?,
    ): RiotMatchData? {
        val matchJson = JSONB.valueOf(match.toString())
        val timelineJson = timeline?.let { JSONB.valueOf(it.toString()) }
        return dsl
            .insertInto(RIOT_MATCH_DATA)
            .set(RIOT_MATCH_DATA.GAME_ID, gameId.value)
            .set(RIOT_MATCH_DATA.MATCH, matchJson)
            .set(RIOT_MATCH_DATA.TIMELINE, timelineJson)
            .onConflict(RIOT_MATCH_DATA.GAME_ID)
            .doUpdate()
            .set(RIOT_MATCH_DATA.MATCH, matchJson)
            .set(RIOT_MATCH_DATA.TIMELINE, timelineJson)
            .set(RIOT_MATCH_DATA.FETCHED_AT, currentInstant())
            .returning()
            .fetchOne()
            ?.let(::recordToRiotMatchData)
    }

    override fun getByGameId(gameId: GameId): RiotMatchData? =
        dsl.fetchOne(RIOT_MATCH_DATA, RIOT_MATCH_DATA.GAME_ID.eq(gameId.value))?.let(::recordToRiotMatchData)

    private fun recordToRiotMatchData(record: RiotMatchDataRecord): RiotMatchData? {
        val gameId = record.gameId?.toGameId() ?: return null
        val match = record.match?.toJsonObject() ?: return null
        val fetchedAt = record.fetchedAt ?: return null
        return RiotMatchData(gameId, match, record.timeline?.toJsonObject(), fetchedAt)
    }

    private fun JSONB.toJsonObject(): JsonObject = Json.parseToJsonElement(data()).jsonObject
}
