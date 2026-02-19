package com.lowbudgetlcs.domain.auth.models

import com.lowbudgetlcs.domain.auth.models.types.SessionId
import com.lowbudgetlcs.domain.auth.models.types.UserId
import com.lowbudgetlcs.domain.auth.models.types.Username
import com.sksamuel.hoplite.Masked
import java.util.UUID

// Type Extensions
fun String.toUsername(): Username = Username(this)

fun String.toMasked(): Masked = Masked(this)

fun Int.toUserId(): UserId = UserId(this)

fun NewUser.toUser(id: UserId): User = User(id, username, passwordHash, isActive, roles)

fun UUID.toSessionId(): SessionId = SessionId(this)
