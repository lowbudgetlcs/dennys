package com.lowbudgetlcs.domain.models.auth

import java.time.Instant

data class NewSession(
    val user: User,
    val expiresAt: Instant,
)
