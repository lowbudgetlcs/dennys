package com.lowbudgetlcs.domain.models

@JvmInline
value class RiotAccountId(val value: Int)

fun Int.toRiotAccountId(): RiotAccountId = RiotAccountId(this)

@JvmInline
value class RiotPuuid(val value: String) {
    init {
        require(value.length == 78) { "Riot puuids must be 78 characters" }
    }
}

data class RiotAccount(
    val id: RiotAccountId,
    val riotPuuid: RiotPuuid,
    val playerId: PlayerId?
)

data class NewRiotAccount(
    val riotPuuid: RiotPuuid,
    val playerId: PlayerId?
)
