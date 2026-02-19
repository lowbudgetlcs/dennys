package com.lowbudgetlcs.domain.models.auth

import com.lowbudgetlcs.domain.models.auth.types.UserId
import com.lowbudgetlcs.domain.models.auth.types.Username

data class User(
    val id: UserId,
    val username: Username,
    val passwordHash: String,
    val isActive: Boolean,
    val roles: Set<String>,
)
