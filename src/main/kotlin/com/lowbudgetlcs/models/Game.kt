package com.lowbudgetlcs.models

import com.lowbudgetlcs.routes.riot.Callback
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

/**
 * Represents a Game. Games are owned by [Series]. When a [Game] is
 * complete, [winner], [loser] and [callbackResult] are guarunteed non-null.
 */
@Serializable
data class Game(
    val id: GameId,
    val shortCode: String,
    val gameNumber: Int,
    val winner: TeamId?,
    val loser: TeamId?,
    val callbackResult: Callback?,
    @Contextual val createdAt: OffsetDateTime,
    val series: SeriesId,
)

/**
 * ID type for [Game]s.
 */
@Serializable
data class GameId(val id: Int)
