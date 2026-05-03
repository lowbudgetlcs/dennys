package com.lowbudgetlcs.domain.auth.core.models

import com.lowbudgetlcs.domain.user.models.User
import java.time.Instant

data class NewSession(
    val user: User,
    val expiresAt: Instant,
)
