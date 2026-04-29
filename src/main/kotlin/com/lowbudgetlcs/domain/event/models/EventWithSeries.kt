package com.lowbudgetlcs.domain.event.models

import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupId
import com.lowbudgetlcs.domain.series.models.Series
import java.time.Instant

data class EventWithSeries(
    val id: EventId,
    val name: EventName,
    val description: EventDescription,
    val eventGroupId: EventGroupId?,
    val riotTournamentId: RiotTournamentId,
    val createdAt: Instant,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus,
    val series: List<Series>,
    val eventStages: Set<EventStage>,
)
