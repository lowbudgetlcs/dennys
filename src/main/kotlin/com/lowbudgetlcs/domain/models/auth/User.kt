package com.lowbudgetlcs.domain.models.auth

@JvmInline
value class UserId(
    val value: Int,
)

fun Int.toUserId(): UserId = UserId(this)

data class User(
    val id: UserId,
    val username: String,
    val passwordHash: String,
    val isActive: Boolean,
    val roles: Set<String>,
)

data class NewUser(
    val username: String,
    val passwordHash: String,
    val roles: Set<String>,
    val isActive: Boolean = true,
)

fun NewUser.toUser(id: UserId): User = User(id, username, passwordHash, isActive, roles)
