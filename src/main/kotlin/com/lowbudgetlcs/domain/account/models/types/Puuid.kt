package com.lowbudgetlcs.domain.account.models.types

const val PUUID_LENGTH = 78

@JvmInline
value class Puuid(
    val value: String,
) {
    init {
        require(value.length == PUUID_LENGTH) { "Puuids must be exactly 78 characters." }
    }
}

// Extensions
fun String.toPuuid(): Puuid = Puuid(this)
