package com.lowbudgetlcs.domain.event.core.model

import com.lowbudgetlcs.domain.event.core.model.types.EventGroupName

data class EventGroupUpdate(
    val name: EventGroupName? = null,
)
