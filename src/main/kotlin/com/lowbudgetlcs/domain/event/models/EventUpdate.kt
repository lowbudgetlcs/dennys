package com.lowbudgetlcs.domain.event.models

import com.lowbudgetlcs.domain.Zeroable
import com.lowbudgetlcs.domain.event.models.types.EventName
import com.lowbudgetlcs.domain.event.models.types.EventStatus
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupId
import java.time.Instant

data class EventUpdate(
    val name: EventName? = null,
    val description: String? = null,
    val startDate: Instant? = null,
    val endDate: Instant? = null,
    val status: EventStatus? = null,
    val eventGroupId: Zeroable<EventGroupId> = Zeroable(null, true),
)
