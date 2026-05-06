package com.lowbudgetlcs.domain.division.core.event.model

import com.lowbudgetlcs.domain.division.core.event.model.types.EventGroupId
import com.lowbudgetlcs.domain.division.core.event.model.types.EventGroupName

data class EventGroupWithEvents(
    val id: EventGroupId,
    val name: EventGroupName,
    val events: List<Event>,
)
