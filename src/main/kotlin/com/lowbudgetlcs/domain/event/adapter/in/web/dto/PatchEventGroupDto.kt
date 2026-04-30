package com.lowbudgetlcs.domain.event.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.event.core.model.EventGroupUpdate
import com.lowbudgetlcs.domain.event.core.model.types.EventGroupName
import kotlinx.serialization.Serializable

@Serializable
data class PatchEventGroupDto(
    val name: String,
)

// Extensions
fun PatchEventGroupDto.toEventGroupUpdate(): EventGroupUpdate =
    EventGroupUpdate(
        name = EventGroupName(name),
    )
