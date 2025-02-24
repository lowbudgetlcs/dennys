package com.lowbudgetlcs.entities

import com.lowbudgetlcs.routes.riot.RiotCallback
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

/**
 * Represents a Game. Games are owned by [Series]. When a [Game] is
 * complete, [winner], [loser] and [callbackResult] are guarunteed non-null.
 */
@Serializable
data class Game(
    override val id: GameId,
    val shortCode: String,
    val gameNumber: Int,
    val winner: TeamId?,
    val loser: TeamId?,
    val callbackResult: RiotCallback?,
    @Contextual val createdAt: OffsetDateTime,
    val series: SeriesId,
) : Entity<GameId>

/**
 * ID type for [Game]s.
 */
@Serializable
data class GameId(val id: Int)
