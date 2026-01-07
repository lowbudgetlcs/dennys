package com.lowbudgetlcs.domain.models.auth

import java.time.Instant

data class AccessToken(
    val tokenHash: String,
    val name: String,
    val userId: UserId,
    val scopes: Set<String>,
    val expiresAt: Instant,
    val createdAt: Instant,
)

data class NewAccessToken(
    val name: String,
    val userId: UserId,
    val scopes: Set<String>,
    val expiresAt: Instant,
)

data class FreshAccessToken(
    val token: String,
)
