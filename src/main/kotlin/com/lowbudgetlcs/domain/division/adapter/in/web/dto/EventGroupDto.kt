package com.lowbudgetlcs.domain.division.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.division.core.model.EventGroup
import kotlinx.serialization.Serializable

@Serializable
data class EventGroupDto(
    val id: Int,
    val name: String,
)

// Extensions
fun EventGroup.toDto(): EventGroupDto =
    EventGroupDto(
        id = id.value,
        name = name.value,
    )
