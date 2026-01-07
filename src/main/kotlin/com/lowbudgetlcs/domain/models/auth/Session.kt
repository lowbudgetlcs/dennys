package com.lowbudgetlcs.domain.models.auth

import java.time.Instant
import java.util.UUID

@JvmInline
value class SessionId(
    val value: UUID,
)

fun UUID.toSessionId(): SessionId = SessionId(this)

data class Session(
    val id: SessionId,
    val userId: UserId,
    val expiresAt: Instant,
)

data class NewSession(
    val user: User,
    val expiresAt: Instant,
)
