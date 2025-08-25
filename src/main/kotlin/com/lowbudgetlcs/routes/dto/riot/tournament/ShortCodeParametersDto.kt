package com.lowbudgetlcs.routes.dto.riot.tournament

import com.lowbudgetlcs.domain.models.riot.tournament.NewShortcode

data class ShortCodeParametersDto(
    val enoughPlayers: Boolean = true,
    val metadata: String = "",
    val mapType: String,
    val pickType: String,
    val spectatorType: String = "ALL",
    val teamSize: Int = 5
)

fun NewShortcode.toShortcodeParametersDto(): ShortCodeParametersDto = ShortCodeParametersDto(
    mapType = mapType.name,
    pickType = pickType.name,
    metadata = metadata
)
