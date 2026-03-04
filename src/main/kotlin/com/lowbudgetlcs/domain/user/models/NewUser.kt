package com.lowbudgetlcs.domain.user.models

import com.lowbudgetlcs.domain.user.models.types.Username

data class NewUser(
    val username: Username,
    val passwordHash: String,
    val roles: Set<String>,
    val isActive: Boolean = true,
)
