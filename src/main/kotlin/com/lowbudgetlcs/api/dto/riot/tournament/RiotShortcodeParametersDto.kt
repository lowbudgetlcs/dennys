package com.lowbudgetlcs.api.dto.riot.tournament

import kotlinx.serialization.Serializable

@Serializable
data class RiotShortcodeParametersDto(
    val enoughPlayers: Boolean = true,
    val metadata: String = "",
    val mapType: String,
    val pickType: String,
    val spectatorType: String = "ALL",
    val teamSize: Int = 5,
)
