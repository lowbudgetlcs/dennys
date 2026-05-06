package com.lowbudgetlcs.domain.division.core.model

import com.lowbudgetlcs.domain.division.core.model.types.EventGroupId
import com.lowbudgetlcs.domain.division.core.model.types.EventGroupName

data class EventGroupWithEvents(
    val id: EventGroupId,
    val name: EventGroupName,
    val events: List<Event>,
)
