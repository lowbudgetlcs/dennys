package com.lowbudgetlcs.domain.event.core.model

import com.lowbudgetlcs.domain.event.core.model.types.EventGroupId
import com.lowbudgetlcs.domain.event.core.model.types.EventGroupName

data class EventGroupWithEvents(
    val id: EventGroupId,
    val name: EventGroupName,
    val events: List<Event>,
)
