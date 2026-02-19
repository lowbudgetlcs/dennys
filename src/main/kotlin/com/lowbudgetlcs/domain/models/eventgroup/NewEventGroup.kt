package com.lowbudgetlcs.domain.models.eventgroup

import com.lowbudgetlcs.domain.models.eventgroup.types.EventGroupName
import com.lowbudgetlcs.domain.models.events.EventId

data class NewEventGroup(
    val name: EventGroupName,
    val events: List<EventId>? = null,
)
