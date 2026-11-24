package com.lowbudgetlcs.domain.stats.models

import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupId
import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.types.TeamId

data class TeamGameStats(
    val gameId: GameId,
    val seriesId: SeriesId,
    val eventId: EventId,
    val eventGroupId: EventGroupId?,
    val stage: EventStage,
    val teamId: TeamId?,
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
