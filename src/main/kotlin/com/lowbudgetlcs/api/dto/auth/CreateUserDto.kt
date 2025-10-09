package com.lowbudgetlcs.api.dto.auth

import kotlinx.serialization.Serializable

@Serializable
/** IMPORTANT: This class is NOT SAFE to print! */
data class CreateUserDto(
    val username: String,
    val password: String,
    val secretKey: String,
)
