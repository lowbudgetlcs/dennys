package com.lowbudgetlcs.domain.auth.models

import com.lowbudgetlcs.domain.auth.models.types.Username

data class NewUser(
    val username: Username,
    val passwordHash: String,
    val roles: Set<String>,
    val isActive: Boolean = true,
)
