package com.lowbudgetlcs.domain.auth.models

import com.lowbudgetlcs.domain.auth.models.types.SessionId
import com.lowbudgetlcs.domain.auth.models.types.UserId
import java.time.Instant

data class Session(
    val id: SessionId,
    val userId: UserId,
    val expiresAt: Instant,
)
