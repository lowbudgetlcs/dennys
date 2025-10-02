package com.lowbudgetlcs.api.dto.events.groups

import kotlinx.serialization.Serializable

@Serializable
data class CreateEventGroupDto(
    val name: String,
    val eventIds: List<Int>? = null,
)
