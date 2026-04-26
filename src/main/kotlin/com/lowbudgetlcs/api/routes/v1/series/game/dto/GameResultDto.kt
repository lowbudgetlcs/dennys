package com.lowbudgetlcs.api.routes.v1.series.game.dto

import kotlinx.serialization.Serializable

@Serializable
data class GameResultDto(val winningTeamId: Int, val losingTeamId: Int)
