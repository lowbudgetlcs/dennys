package com.lowbudgetlcs.api.routes.v1.event.group.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateEventGroupDto(
    val name: String,
    val eventIds: List<Int>? = null,
)
