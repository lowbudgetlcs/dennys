package com.lowbudgetlcs.domain.player.core.model.types

const val PLAYER_NAME_MAX_LENGTH = 20
const val PLAYER_NAME_MIN_LENGTH = 3

@JvmInline
value class PlayerName(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "Name cannot be blank." }
        require(value.length <= PLAYER_NAME_MAX_LENGTH) { "Name must not exceed $PLAYER_NAME_MAX_LENGTH characters." }
        require(value.length >= PLAYER_NAME_MIN_LENGTH) { "Name must be at least $PLAYER_NAME_MAX_LENGTH characters." }
    }
}

// Extensions
fun String.toPlayerName(): PlayerName = PlayerName(this)
