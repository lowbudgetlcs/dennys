package com.lowbudgetlcs.api.routes.v1.event.group.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventGroupDto(
    val id: Int,
    val name: String,
)
