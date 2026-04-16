package com.lowbudgetlcs.domain.event.models

import com.lowbudgetlcs.domain.event.models.types.EventDescription
import com.lowbudgetlcs.domain.event.models.types.EventName
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupId
import java.time.Instant

data class NewEvent(
    val name: EventName,
    val description: EventDescription,
    val startDate: Instant,
    val endDate: Instant,
    val status: EventStatus,
    val eventGroupId: EventGroupId? = null,
    val eventStages: Set<EventStage>,
)
