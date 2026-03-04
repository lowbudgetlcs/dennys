package com.lowbudgetlcs.domain.eventgroup.models

import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupName

data class NewEventGroup(
    val name: EventGroupName,
    val events: List<EventId>? = null,
)
