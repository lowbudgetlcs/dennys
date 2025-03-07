package com.lowbudgetlcs.models

import kotlinx.serialization.Serializable

/**
 * Represents a series. When a series is complete, [winner]
 * and [loser] are guarunteed non-null.
 */
@Serializable
data class Series(
    val id: SeriesId,
    val division: DivisionId,
    val winner: TeamId?,
    val loser: TeamId?,
)

/**
 * ID type for [Series].
 */
@Serializable
data class SeriesId(val id: Int)