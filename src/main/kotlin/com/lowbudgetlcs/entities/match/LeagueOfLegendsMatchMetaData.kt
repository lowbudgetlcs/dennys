package com.lowbudgetlcs.entities.match

import kotlinx.serialization.Serializable

@Serializable
data class LeagueOfLegendsMatchMetaData(
    val dataVersion: Int,
    val matchId: String,
    val participants: List<String>
)
