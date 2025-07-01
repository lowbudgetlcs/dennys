package com.lowbudgetlcs.models.tournament

import kotlinx.serialization.Serializable

@Serializable
data class CodesRequest(
    val enoughPlayers: Boolean = true,
    val mapType: String = "SUMMONERS_RIFT",
    val metaData: String = "",
    val pickType: String = "TOURNAMENT_DRAFT",
    val spectatorType: String = "ALL",
    val teamSize: Int = 5
)
