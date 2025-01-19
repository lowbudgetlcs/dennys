package com.lowbudgetlcs.models

import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val id: TeamId, val name: String, val logo: String?, val captain: PlayerId?, val division: DivisionId?
)

@Serializable
data class TeamId(val id: Int)
