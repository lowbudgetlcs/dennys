package com.lowbudgetlcs.domain.division.core.event.model

import com.lowbudgetlcs.domain.division.core.event.model.enums.EventStage
import com.lowbudgetlcs.domain.division.core.event.model.enums.EventStatus
import com.lowbudgetlcs.domain.division.core.event.model.types.EventDescription
import com.lowbudgetlcs.domain.division.core.event.model.types.EventGroupId
import com.lowbudgetlcs.domain.division.core.event.model.types.EventId
import com.lowbudgetlcs.domain.division.core.event.model.types.EventName
import java.time.Instant

data class NewEvent(
    val name: EventName,
    val description: EventDescription,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus,
    val eventGroupId: EventGroupId? = null,
    val eventStages: Set<EventStage>,
) {
}

// Extensions
fun NewEvent.toEvent(id: EventId, createdAt: Instant, riotTournamentId: RiotTournamentId): Event = Event(
    id = id,
    name = this.name,
    description = this.description,
    eventGroupId = this.eventGroupId,
    riotTournamentId = riotTournamentId,
    createdAt = createdAt,
    startDate = this.startDate,
    endDate = this.endDate,
    status = this.status,
    eventStages = this.eventStages
)
