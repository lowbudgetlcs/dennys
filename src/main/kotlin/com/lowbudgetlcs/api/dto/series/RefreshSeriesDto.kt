package com.lowbudgetlcs.api.dto.series

import kotlinx.serialization.Serializable

/**
 * Identifies the single tournament code to re-check against Riot. Exactly one field must be set;
 * both or neither is a 422.
 */
@Serializable
data class RefreshSeriesDto(
    val tournamentCodeId: Int? = null,
    val shortcode: String? = null,
)
