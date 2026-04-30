package com.lowbudgetlcs.domain.series.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.series.core.model.Game
import kotlinx.serialization.Serializable

@Serializable
data class GameDto(
    val id: Int,
    val shortcode: String,
    val blueTeamId: Int,
    val redTeamId: Int,
    val seriesId: Int,
    val number: Int,
)

// Extensions
fun Game.toDto(): GameDto =
    GameDto(
        id = id.value,
        shortcode = shortcode.value,
        blueTeamId = blueTeamId.value,
        redTeamId = redTeamId.value,
        seriesId = seriesId.value,
        number = number,
    )
