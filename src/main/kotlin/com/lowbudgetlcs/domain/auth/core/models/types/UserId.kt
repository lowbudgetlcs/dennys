package com.lowbudgetlcs.domain.auth.core.models.types

@JvmInline
value class UserId(
    val value: Int,
)

fun Int.toUserId(): UserId = UserId(this)
