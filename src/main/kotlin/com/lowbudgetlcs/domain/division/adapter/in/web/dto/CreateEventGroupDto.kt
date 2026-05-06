package com.lowbudgetlcs.domain.division.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.division.core.event.model.NewEventGroup
import com.lowbudgetlcs.domain.division.core.event.model.types.EventGroupName
import com.lowbudgetlcs.domain.division.core.event.model.types.toEventId
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
