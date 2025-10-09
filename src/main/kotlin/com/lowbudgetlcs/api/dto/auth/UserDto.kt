package com.lowbudgetlcs.api.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val username: String,
)
