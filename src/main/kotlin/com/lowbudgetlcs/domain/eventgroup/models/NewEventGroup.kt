package com.lowbudgetlcs.domain.eventgroup.models

import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupId
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupName

data class NewEventGroup(
    val name: EventGroupName,
    val events: List<EventId>? = null,
)

// Extensions
fun NewEventGroup.toEventGroup(id: EventGroupId): EventGroup =
    EventGroup(
        id = id,
        name = name,
    )
