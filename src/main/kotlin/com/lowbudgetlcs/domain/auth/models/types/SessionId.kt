package com.lowbudgetlcs.domain.auth.models.types

import java.util.UUID

@JvmInline
value class SessionId(
    val value: UUID,
)
