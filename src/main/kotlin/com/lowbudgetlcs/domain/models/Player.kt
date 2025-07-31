package com.lowbudgetlcs.domain.models

@JvmInline
value class PlayerId(val value: Int)

fun Int.toPlayerId(): PlayerId = PlayerId(this)

@JvmInline
value class PlayerName(val name: String) {
    init {
        require(name.isNotBlank()) { "Player name must not be blank" }
    }
}


fun String.toPlayerName(): PlayerName = PlayerName(this)

data class Player(
    val id: PlayerId,
    val name: PlayerName
)

data class NewPlayer(
    val name: PlayerName,
    val teamId: TeamId?
)

data class PlayerWithAccounts(
    val id: PlayerId,
    val name: PlayerName,
    val accounts: List<RiotAccount>
)
