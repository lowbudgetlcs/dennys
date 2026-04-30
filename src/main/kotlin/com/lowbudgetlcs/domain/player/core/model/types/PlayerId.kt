package com.lowbudgetlcs.domain.player.core.model.types

@JvmInline
value class PlayerId(
    val value: Int,
)

// Extensions
fun Int.toPlayerId(): PlayerId = PlayerId(this)
