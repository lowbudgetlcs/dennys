package com.lowbudgetlcs.domain.division.core.series.model.types

@JvmInline
value class GameId(
    val value: Int,
)

// Extensions
fun Int.toGameId(): GameId = GameId(this)
