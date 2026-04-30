package com.lowbudgetlcs.domain.account.models.types

@JvmInline
value class AccountId(
    val value: Int,
)

// Extensions
fun Int.toAccountId(): AccountId = AccountId(this)
