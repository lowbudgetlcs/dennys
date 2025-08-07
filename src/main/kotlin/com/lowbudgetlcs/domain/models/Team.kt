package com.lowbudgetlcs.domain.models

import com.lowbudgetlcs.domain.models.events.EventId

@JvmInline
value class TeamId(val value: Int)

fun Int.toTeamId(): TeamId = TeamId(this)

@JvmInline
value class TeamName(val value: String) {
    init {
        require(value.length < 80) { "Team name must be less than 80 characters" }
    }
}

@JvmInline
value class TeamLogoName(val value: String)

fun String.toTeamName(): TeamName = TeamName(this)
fun String.toTeamLogoName(): TeamLogoName = TeamLogoName(this)

data class Team(
    val id: TeamId,
    val name: TeamName,
    val logoName: TeamLogoName?,
    val eventId: EventId?
)

data class NewTeam(
    val name: TeamName,
    val logoName: String?
)

data class TeamWithPlayers(
    val id: TeamId,
    val name: TeamName,
    val logoName: TeamLogoName?,
    val eventId: EventId?,
    val players: List<Player>
)

