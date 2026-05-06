package com.lowbudgetlcs.domain.division.core.event.model

import com.lowbudgetlcs.domain.division.core.event.model.types.EventGroupName

data class EventGroupUpdate(
    val name: EventGroupName? = null,
)
