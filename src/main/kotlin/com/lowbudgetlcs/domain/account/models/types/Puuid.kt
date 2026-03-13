package com.lowbudgetlcs.domain.account.models.types

@JvmInline
value class Puuid(
    val value: String,
) {
    init {
        require(value.length == 78) { "Puuids must be exactly 78 characters." }
    }
}
