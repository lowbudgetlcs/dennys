package com.lowbudgetlcs.domain.eventgroup.models

import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupId
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupName

data class EventGroup(
    val id: EventGroupId,
    val name: EventGroupName,
)
