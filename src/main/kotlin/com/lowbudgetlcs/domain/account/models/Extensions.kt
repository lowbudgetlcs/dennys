package com.lowbudgetlcs.domain.account.models

import com.lowbudgetlcs.domain.account.models.types.AccountId
import com.lowbudgetlcs.domain.account.models.types.Puuid

// Type Extensions
fun String.toPuuid(): Puuid = Puuid(this)

fun Int.toAccountId(): AccountId = AccountId(this)
