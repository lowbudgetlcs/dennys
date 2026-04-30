package com.lowbudgetlcs.domain.event.models

import com.lowbudgetlcs.domain.event.models.enums.EventStage
import com.lowbudgetlcs.domain.event.models.enums.EventStatus
import com.lowbudgetlcs.domain.event.models.types.EventDescription
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventName
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupId
import com.lowbudgetlcs.domain.team.models.Team
import java.time.Instant

data class EventWithTeams(
    val id: EventId,
    val name: EventName,
    val description: EventDescription,
    val eventGroupId: EventGroupId?,
    val riotTournamentId: RiotTournamentId,
    val createdAt: Instant,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus,
    val teams: List<Team>,
    val eventStages: Set<EventStage>,
)
