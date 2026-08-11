package com.lowbudgetlcs.api.dto.games

import com.lowbudgetlcs.serializers.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class GameDto(
    val id: Int,
    val seriesId: Int,
    val tournamentCodeId: Int?,
    val riotMatchId: String?,
    val number: Int,
    @Serializable(with = InstantSerializer::class) val createdAt: Instant,
    val result: GameResultDto?,
)

@Serializable
data class GameResultDto(
    val winningTeamId: Int,
    val losingTeamId: Int,
)
