package com.lowbudgetlcs.domain.event.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.event.core.model.NewEventGroup
import com.lowbudgetlcs.domain.event.core.model.types.EventGroupName
import com.lowbudgetlcs.domain.event.core.model.types.toEventId
import kotlinx.serialization.Serializable

@Serializable
data class CreateEventGroupDto(
    val name: String,
    val eventIds: List<Int>? = null,
)

// Extensions
fun CreateEventGroupDto.toNewEventGroup(): NewEventGroup =
    NewEventGroup(
        name = EventGroupName(name),
        events = eventIds?.map { it.toEventId() },
    )
