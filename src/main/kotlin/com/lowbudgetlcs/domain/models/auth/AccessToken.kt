package com.lowbudgetlcs.domain.models.auth

import com.lowbudgetlcs.domain.models.auth.types.UserId
import java.time.Instant

data class AccessToken(
    val tokenHash: String,
    val name: String,
    val userId: UserId,
    val scopes: Set<String>,
    val expiresAt: Instant,
    val createdAt: Instant,
)
