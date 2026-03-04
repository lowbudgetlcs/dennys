package com.lowbudgetlcs.domain.eventgroup.models

import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupId
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupName

data class EventGroupWithEvents(
    val id: EventGroupId,
    val name: EventGroupName,
    val events: List<Event>,
)
