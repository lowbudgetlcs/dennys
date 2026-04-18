package com.lowbudgetlcs.domain.player.models.types

const val PLAYER_NAME_MAX_LENGTH = 20
<<<<<<< HEAD
const val PLAYER_NAME_MIN_LENGTH = 3
=======
>>>>>>> release/1.3.1

@JvmInline
value class PlayerName(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "Name cannot be blank." }
<<<<<<< HEAD
        require(value.length <= PLAYER_NAME_MAX_LENGTH) { "Name must not exceed $PLAYER_NAME_MAX_LENGTH characters." }
        require(value.length >= PLAYER_NAME_MIN_LENGTH) { "Name must be at least $PLAYER_NAME_MAX_LENGTH characters." }
=======
        require(value.length <= PLAYER_NAME_MAX_LENGTH) { "Name must be shorter than $PLAYER_NAME_MAX_LENGTH." }
>>>>>>> release/1.3.1
    }
}
