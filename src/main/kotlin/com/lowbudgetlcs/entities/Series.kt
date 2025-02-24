package com.lowbudgetlcs.entities

import kotlinx.serialization.Serializable

@Serializable
data class Series(
    override val id: SeriesId,
    val division: DivisionId,
    val winner: TeamId?,
    val loser: TeamId?,
) : Entity<SeriesId>

@Serializable
data class SeriesId(val id: Int)