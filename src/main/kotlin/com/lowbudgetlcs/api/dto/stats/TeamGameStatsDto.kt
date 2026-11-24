package com.lowbudgetlcs.api.dto.stats

import com.lowbudgetlcs.domain.event.models.types.EventStage
import kotlinx.serialization.Serializable

@Serializable
data class TeamGameStatsDto(
    val gameId: Int,
    val seriesId: Int,
    val eventId: Int,
    val eventGroupId: Int?,
    val stage: EventStage,
    val teamId: Int?,
    val side: String?,
    val gameDurationS: Int,
    val win: Boolean,
    val firstBlood: Boolean,
    val firstTower: Boolean,
    val firstDragon: Boolean,
    val firstBaron: Boolean,
    val kills: Int,
    val towerKills: Int,
    val inhibitorKills: Int,
    val dragonKills: Int,
    val baronKills: Int,
    val riftHeraldKills: Int,
    val hordeKills: Int,
    val bans: List<Int>,
)
