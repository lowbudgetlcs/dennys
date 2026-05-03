package com.lowbudgetlcs.domain.auth.core.models.types

import java.util.UUID

@JvmInline
value class SessionId(
    val value: UUID,
)

// Extensions
fun UUID.toSessionId(): SessionId = SessionId(this)
