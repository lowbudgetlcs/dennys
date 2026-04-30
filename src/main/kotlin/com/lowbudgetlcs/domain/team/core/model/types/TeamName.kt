package com.lowbudgetlcs.domain.team.core.model.types

const val TEAM_NAME_MAX_LENGTH = 80
const val TEAM_NAME_MIN_LENGTH = 3

@JvmInline
value class TeamName(
    val value: String,
) {
    fun contains(other: TeamName): Boolean = value.uppercase().contains(other.value.uppercase())
    fun contains(other: String): Boolean = value.uppercase().contains(other.uppercase())

    init {
        require(value.isNotEmpty()) { "Team name cannot be blank." }
        require(value.length <= TEAM_NAME_MAX_LENGTH) { "Team name cannot exceed $TEAM_NAME_MAX_LENGTH characters." }
        require(value.length >= TEAM_NAME_MIN_LENGTH) { "Team name must be at least $TEAM_NAME_MIN_LENGTH characters." }
    }
}

// Extensions
fun String.toTeamName(): TeamName = TeamName(this)
