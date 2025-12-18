package com.lowbudgetlcs.domain.models.auth

@JvmInline
value class SessionId(
    val value: Int,
)

fun Int.toSessionId(): SessionId = SessionId(this)

data class Session(
    val id: SessionId,
    val userId: UserId,
)
