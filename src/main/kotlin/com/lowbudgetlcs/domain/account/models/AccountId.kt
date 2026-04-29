package com.lowbudgetlcs.domain.account.models

@JvmInline
value class AccountId(
    val value: Int,
)

// Extensions
fun Int.toAccountId(): AccountId = AccountId(this)
