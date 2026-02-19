package com.lowbudgetlcs.domain.models.auth

import com.lowbudgetlcs.domain.models.auth.types.SessionId
import com.lowbudgetlcs.domain.models.auth.types.UserId
import java.time.Instant

data class Session(
    val id: SessionId,
    val userId: UserId,
    val expiresAt: Instant,
)
