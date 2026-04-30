package com.lowbudgetlcs.domain.series.core.model.types

@JvmInline
value class GameId(
    val value: Int,
)

// Extensions
fun Int.toGameId(): GameId = GameId(this)
