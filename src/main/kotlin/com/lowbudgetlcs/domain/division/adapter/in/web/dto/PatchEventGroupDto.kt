package com.lowbudgetlcs.domain.division.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.division.core.event.model.EventGroupUpdate
import com.lowbudgetlcs.domain.division.core.event.model.types.EventGroupName
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
