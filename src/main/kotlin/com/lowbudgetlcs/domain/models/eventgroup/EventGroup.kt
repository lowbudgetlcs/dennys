package com.lowbudgetlcs.domain.models.eventgroup

import com.lowbudgetlcs.domain.models.eventgroup.types.EventGroupId
import com.lowbudgetlcs.domain.models.eventgroup.types.EventGroupName

data class EventGroup(
    val id: EventGroupId,
    val name: EventGroupName,
)
