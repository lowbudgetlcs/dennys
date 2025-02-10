package com.lowbudgetlcs.entities

import kotlinx.serialization.Serializable

@Serializable
data class SeriesId(val id: Int)

@Serializable
data class Series(
    val id: SeriesId,
    val division: DivisionId,
    val winner: TeamId?,
    val loser: TeamId?,
)

