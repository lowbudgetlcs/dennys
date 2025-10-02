package com.lowbudgetlcs.api.dto.events.groups

import kotlinx.serialization.Serializable

@Serializable
data class EventGroupDto(
    val id: Int,
    val name: String,
)
