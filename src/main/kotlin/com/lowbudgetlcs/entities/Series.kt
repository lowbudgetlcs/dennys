package com.lowbudgetlcs.entities

import kotlinx.serialization.Serializable

/**
 * Represents a series. When a series is complete, [winner]
 * and [loser] are guarunteed non-null.
 */
@Serializable
data class Series(
    override val id: SeriesId,
    val division: DivisionId,
    val winner: TeamId?,
    val loser: TeamId?,
) : Entity<SeriesId>

/**
 * ID type for [Series].
 */
@Serializable
data class SeriesId(val id: Int)