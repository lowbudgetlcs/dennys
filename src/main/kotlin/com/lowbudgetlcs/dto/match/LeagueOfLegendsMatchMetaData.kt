package com.lowbudgetlcs.models.match

import kotlinx.serialization.Serializable

@Serializable
data class LeagueOfLegendsMatchMetaData(
    val dataVersion: Int,
    val matchId: String,
    val participants: List<String>
)
