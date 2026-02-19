package com.lowbudgetlcs.domain.eventgroup.models

import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupName
import com.lowbudgetlcs.domain.models.events.EventId

data class NewEventGroup(
    val name: EventGroupName,
    val events: List<EventId>? = null,
)
