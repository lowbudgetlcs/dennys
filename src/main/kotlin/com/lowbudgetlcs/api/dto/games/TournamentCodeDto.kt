package com.lowbudgetlcs.api.dto.games

import com.lowbudgetlcs.serializers.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class TournamentCodeDto(
    val id: Int,
    val shortcode: String,
    val seriesId: Int,
    val blueTeamId: Int,
    val redTeamId: Int,
    @Serializable(with = InstantSerializer::class) val createdAt: Instant,
)
