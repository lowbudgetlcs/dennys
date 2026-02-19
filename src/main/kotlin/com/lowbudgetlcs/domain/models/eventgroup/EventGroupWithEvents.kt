package com.lowbudgetlcs.domain.models.eventgroup

import com.lowbudgetlcs.domain.models.eventgroup.types.EventGroupId
import com.lowbudgetlcs.domain.models.eventgroup.types.EventGroupName
import com.lowbudgetlcs.domain.models.events.Event

data class EventGroupWithEvents(
    val id: EventGroupId,
    val name: EventGroupName,
    val events: List<Event>,
)
