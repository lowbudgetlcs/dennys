package com.lowbudgetlcs.domain.stats.models

import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupId
import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.domain.team.models.types.TeamId

data class StatsQuery(
    val eventGroupId: EventGroupId? = null,
    val eventId: EventId? = null,
    val stage: EventStage? = null,
    val teamId: TeamId? = null,
    val playerId: PlayerId? = null,
    val championId: Int? = null,
)
