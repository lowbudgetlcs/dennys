package com.lowbudgetlcs.domain.models

import com.lowbudgetlcs.domain.models.riot.tournament.Shortcode
import com.lowbudgetlcs.domain.models.team.TeamId

@JvmInline
value class GameId(
    val value: Int,
)

fun Int.toGameId(): GameId = GameId(this)

data class GameResult(
    val winningTeamId: TeamId,
    val losingTeamId: TeamId,
)

data class Game(
    val id: GameId,
    val shortcode: Shortcode,
    val blueTeamId: TeamId,
    val redTeamId: TeamId,
    val seriesId: SeriesId,
    val number: Int,
    val result: GameResult?,
)

data class NewGame(
    val blueTeamId: TeamId,
    val redTeamId: TeamId,
)
