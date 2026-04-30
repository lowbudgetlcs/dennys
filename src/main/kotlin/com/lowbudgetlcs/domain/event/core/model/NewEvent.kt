package com.lowbudgetlcs.domain.event.core.model

import com.lowbudgetlcs.domain.event.core.model.enums.EventStage
import com.lowbudgetlcs.domain.event.core.model.enums.EventStatus
import com.lowbudgetlcs.domain.event.core.model.types.EventDescription
import com.lowbudgetlcs.domain.event.core.model.types.EventName
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
