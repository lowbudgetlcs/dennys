package com.lowbudgetlcs.api.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginDto(
    val username: String,
    val password: String,
)
