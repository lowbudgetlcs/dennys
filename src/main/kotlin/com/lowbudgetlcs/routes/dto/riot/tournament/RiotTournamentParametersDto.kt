package com.lowbudgetlcs.routes.dto.riot.tournament

import kotlinx.serialization.Serializable

@Serializable
data class RiotTournamentParametersDto(val name: String, val providerId: Int)
