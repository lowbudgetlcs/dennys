package com.lowbudgetlcs.domain.division.core.event.model

import com.lowbudgetlcs.domain.division.core.event.model.enums.EventStage
import com.lowbudgetlcs.domain.division.core.event.model.enums.EventStatus
import com.lowbudgetlcs.domain.division.core.event.model.types.EventDescription
import com.lowbudgetlcs.domain.division.core.event.model.types.EventGroupId
import com.lowbudgetlcs.domain.division.core.event.model.types.EventId
import com.lowbudgetlcs.domain.division.core.event.model.types.EventName
import com.lowbudgetlcs.domain.division.core.series.model.Series
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
