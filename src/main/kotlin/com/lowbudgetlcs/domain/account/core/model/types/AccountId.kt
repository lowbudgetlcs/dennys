package com.lowbudgetlcs.domain.account.core.model.types

@JvmInline
value class AccountId(
    val value: Int,
)

// Extensions
fun Int.toAccountId(): AccountId = AccountId(this)
