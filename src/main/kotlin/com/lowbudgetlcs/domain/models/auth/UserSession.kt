package com.lowbudgetlcs.domain.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val sessionId: Int,
    val userId: Int,
    val username: String,
)
