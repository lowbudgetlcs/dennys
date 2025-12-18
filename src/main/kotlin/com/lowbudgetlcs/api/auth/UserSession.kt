package com.lowbudgetlcs.api.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val id: Int,
    val userId: Int,
)
