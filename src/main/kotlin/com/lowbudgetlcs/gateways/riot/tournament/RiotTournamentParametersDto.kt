package com.lowbudgetlcs.gateways.riot.tournament

import kotlinx.serialization.Serializable

@Serializable
data class RiotTournamentParametersDto(
    val name: String,
    val providerId: Int,
)
