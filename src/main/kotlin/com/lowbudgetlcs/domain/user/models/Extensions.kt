package com.lowbudgetlcs.domain.user.models

import com.lowbudgetlcs.domain.user.models.types.UserId
import com.lowbudgetlcs.domain.user.models.types.Username

fun String.toUsername(): Username = Username(this)

// Type Extensions
fun Int.toUserId(): UserId = UserId(this)

// Class Extensions
fun NewUser.toUser(id: UserId): User = User(id, username, passwordHash, isActive, roles)
