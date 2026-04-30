package com.lowbudgetlcs.domain.event.core.model

import com.lowbudgetlcs.domain.event.core.model.types.EventGroupId
import com.lowbudgetlcs.domain.event.core.model.types.EventGroupName
import com.lowbudgetlcs.domain.event.core.model.types.EventId

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
