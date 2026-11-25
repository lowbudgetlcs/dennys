package com.lowbudgetlcs.api.dto.stats

import com.lowbudgetlcs.domain.event.models.types.EventStage
import kotlinx.serialization.Serializable

@Serializable
data class PlayerGameStatsDto(
    val gameId: Int,
    val seriesId: Int,
    val eventId: Int,
    val eventGroupId: Int?,
    val stage: EventStage,
    val teamId: Int?,
    val playerId: Int?,
    val gameDurationSeconds: Int,
    val puuid: String?,
    val riotIdGameName: String?,
    val riotIdTagline: String?,
    val championId: Int,
    val championName: String?,
    val position: String?,
    val win: Boolean,
    val kills: Int,
    val deaths: Int,
    val assists: Int,
    val cs: Int,
    val goldEarned: Int,
    val champLevel: Int,
    val damageToChampions: Int,
    val damageTaken: Int,
    val visionScore: Int,
    val wardsPlaced: Int,
    val wardsKilled: Int,
    val controlWardsBought: Int,
    val voidGrubs: Int,
)
