package com.lowbudgetlcs.api.dto.eventgroup

import kotlinx.serialization.Serializable

@Serializable
data class EventGroupDto(
    val id: Int,
    val name: String,
)
