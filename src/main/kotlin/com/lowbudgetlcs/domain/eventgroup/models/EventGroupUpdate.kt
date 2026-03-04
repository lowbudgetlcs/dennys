package com.lowbudgetlcs.domain.eventgroup.models

import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupName

data class EventGroupUpdate(
    val name: EventGroupName? = null,
)
