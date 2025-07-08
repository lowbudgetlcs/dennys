package com.lowbudgetlcs.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class Game(
    val shortCode: String,
    val blueSide: Team,
    val redSide: Team,
    val series: Series,
)

/**
 * ID type for [Game]s.
 */
@Serializable
data class GameId(val id: Int)
