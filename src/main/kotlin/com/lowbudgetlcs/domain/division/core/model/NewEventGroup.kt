package com.lowbudgetlcs.domain.division.core.model

import com.lowbudgetlcs.domain.division.core.model.types.EventGroupId
import com.lowbudgetlcs.domain.division.core.model.types.EventGroupName
import com.lowbudgetlcs.domain.division.core.model.types.EventId

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
