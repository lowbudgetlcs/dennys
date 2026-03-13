package com.lowbudgetlcs.domain.user.models

import com.lowbudgetlcs.domain.user.models.types.UserId
import com.lowbudgetlcs.domain.user.models.types.Username

data class User(
    val id: UserId,
    val username: Username,
    val passwordHash: String,
    val isActive: Boolean,
    val roles: Set<String>,
)
