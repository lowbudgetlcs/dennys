package com.lowbudgetlcs.domain.division.core.model

import com.lowbudgetlcs.domain.division.core.model.enums.EventStage
import com.lowbudgetlcs.domain.division.core.model.enums.EventStatus
import com.lowbudgetlcs.domain.division.core.model.types.EventDescription
import com.lowbudgetlcs.domain.division.core.model.types.EventGroupId
import com.lowbudgetlcs.domain.division.core.model.types.EventId
import com.lowbudgetlcs.domain.division.core.model.types.EventName
import com.lowbudgetlcs.domain.team.core.model.Team
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
