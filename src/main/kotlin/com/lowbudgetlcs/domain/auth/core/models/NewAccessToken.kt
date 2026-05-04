package com.lowbudgetlcs.domain.auth.core.models

import com.lowbudgetlcs.domain.auth.core.models.types.UserId
import java.time.Instant

data class NewAccessToken(
    val name: String,
    val userId: UserId,
    val scopes: Set<String>,
    val expiresAt: Instant,
)
