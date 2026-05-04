package com.lowbudgetlcs.domain.auth.core.models

import java.time.Instant

data class NewSession(
    val user: User,
    val expiresAt: Instant,
)
