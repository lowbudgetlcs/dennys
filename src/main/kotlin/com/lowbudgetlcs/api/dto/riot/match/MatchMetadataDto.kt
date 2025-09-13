package com.lowbudgetlcs.api.dto.riot.match

import kotlinx.serialization.Serializable

@Serializable
data class MatchMetadataDto(
    val dataVersion: Int,
    val matchId: String,
    val participants: List<String>,
)
