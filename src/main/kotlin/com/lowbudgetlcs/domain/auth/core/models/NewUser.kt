package com.lowbudgetlcs.domain.auth.core.models

import com.lowbudgetlcs.domain.auth.core.models.types.UserId
import com.lowbudgetlcs.domain.auth.core.models.types.Username

data class NewUser(
    val username: Username,
    val passwordHash: String,
    val roles: Set<String>,
    val isActive: Boolean = true,
)

// Extensions
fun NewUser.toUser(id: UserId): User = User(id, username, passwordHash, isActive, roles)
