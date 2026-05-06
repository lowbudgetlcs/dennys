package com.lowbudgetlcs.domain.division.core.event.model

import com.lowbudgetlcs.domain.division.core.event.model.types.EventGroupId
import com.lowbudgetlcs.domain.division.core.event.model.types.EventGroupName
import com.lowbudgetlcs.domain.division.core.event.model.types.EventId

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
