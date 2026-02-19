package com.lowbudgetlcs.domain.models.auth

import com.lowbudgetlcs.domain.models.auth.types.SessionId
import com.lowbudgetlcs.domain.models.auth.types.UserId
import com.lowbudgetlcs.domain.models.auth.types.Username
import com.sksamuel.hoplite.Masked
import java.util.UUID

fun String.toUsername(): Username = Username(this)

fun String.toMasked(): Masked = Masked(this)

fun Int.toUserId(): UserId = UserId(this)

fun NewUser.toUser(id: UserId): User = User(id, username, passwordHash, isActive, roles)

fun UUID.toSessionId(): SessionId = SessionId(this)
