package com.lowbudgetlcs.domain.models.player

import com.lowbudgetlcs.domain.models.riot.account.RiotAccount

@JvmInline
value class PlayerId(
    val value: Int,
)

fun Int.toPlayerId(): PlayerId = PlayerId(this)

@JvmInline
value class PlayerName(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "Player name cannot be blank" }
    }
}

fun String.toPlayerName(): PlayerName = PlayerName(this)

data class Player(
    val id: PlayerId,
    val name: PlayerName,
)

data class NewPlayer(
    val name: PlayerName,
)

data class PlayerWithAccounts(
    val id: PlayerId,
    val name: PlayerName,
    val accounts: List<RiotAccount>,
)
