package com.lowbudgetlcs.domain.models

@JvmInline
value class TeamId(val value: Int)

@JvmInline
value class TeamName(val value: String) {
    init {
        require(value.length < 80) { "Team name must be less than 80 characters" }
    }
}

data class Team(
    val id: TeamId,
    val name: TeamName,
    val logoName: String?,
    val eventId: EventId?,
)

data class NewTeam(
    val name: TeamName,
    val logoName: String?,
    val eventId: EventId?,
)
