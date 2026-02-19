package com.lowbudgetlcs.domain.models.eventgroup

import com.lowbudgetlcs.domain.models.eventgroup.types.EventGroupName

data class EventGroupUpdate(
    val name: EventGroupName? = null,
)
