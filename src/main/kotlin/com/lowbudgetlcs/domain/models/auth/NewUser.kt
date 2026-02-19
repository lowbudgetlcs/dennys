package com.lowbudgetlcs.domain.models.auth

import com.lowbudgetlcs.domain.models.auth.types.Username

data class NewUser(
    val username: Username,
    val passwordHash: String,
    val roles: Set<String>,
    val isActive: Boolean = true,
)
