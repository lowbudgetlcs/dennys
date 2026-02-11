package com.lowbudgetlcs.domain.models.player.account

fun String.toPuuid(): Puuid = Puuid(this)

fun Int.toAccountId(): AccountId = AccountId(this)
