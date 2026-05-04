package com.lowbudgetlcs.domain.auth.core.models

import com.lowbudgetlcs.domain.auth.core.models.types.UserId
import com.lowbudgetlcs.domain.auth.core.models.types.Username

data class User(
    val id: UserId,
    val username: Username,
    val passwordHash: String,
    val isActive: Boolean,
    val roles: Set<String>,
)
