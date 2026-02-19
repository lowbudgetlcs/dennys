package com.lowbudgetlcs.domain.models.auth.types

import java.util.UUID

@JvmInline
value class SessionId(
    val value: UUID,
)
