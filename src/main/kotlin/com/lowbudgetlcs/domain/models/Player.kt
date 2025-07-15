package com.lowbudgetlcs.domain.models

@JvmInline
value class PlayerId(val value: Int)

@JvmInline
value class PlayerName(val name: String) {
    init {
        // Riot IDs are of the form ruuffian#FUNZ
        require(name.contains("#")) { "Invalid name: Missing '#'" }
    }
}

data class Player(
    val id: PlayerId, val name: PlayerName
)

data class NewPlayer(
    val name: PlayerName, val teamId: TeamId?
)
