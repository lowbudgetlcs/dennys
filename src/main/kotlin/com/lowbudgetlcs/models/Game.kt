package com.lowbudgetlcs.models

import com.lowbudgetlcs.routes.riot.RiotCallback
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class Game(
    val id: GameId,
    val shortCode: String,
    val gameNumber: Int,
    val winner: TeamId?,
    val loser: TeamId?,
    val callbackResult: RiotCallback?,
    @Contextual
    val createdAt: OffsetDateTime,
    val series: SeriesId,
)

@Serializable
data class GameId(val id: Int)