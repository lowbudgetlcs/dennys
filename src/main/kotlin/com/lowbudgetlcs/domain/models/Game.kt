package com.lowbudgetlcs.domain.models

@JvmInline
value class GameId(val value: Int)

fun Int.toGameId(): GameId = GameId(this)

data class Game(
    val id: GameId,
    val shortCode: String,
    val blueSideId: TeamId,
    val redSideId: TeamId,
    val seriesId: SeriesId,
    val winnerId: TeamId?,
    val loserId: TeamId?
)

data class NewGame(
    val blueSideId: TeamId,
    val redSideId: TeamId,
    val seriesId: Int,
)
