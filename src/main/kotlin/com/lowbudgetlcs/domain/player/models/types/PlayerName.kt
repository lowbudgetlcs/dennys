package com.lowbudgetlcs.domain.player.models.types

const val PLAYER_NAME_MAX_LENGTH = 20

@JvmInline
value class PlayerName(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "Name cannot be blank." }
        require(value.length <= PLAYER_NAME_MAX_LENGTH) { "Name must be shorter than $PLAYER_NAME_MAX_LENGTH." }
    }
}
