package com.lowbudgetlcs.domain.player.models.types

@JvmInline
value class PlayerName(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "Player name cannot be blank." }
    }
}
