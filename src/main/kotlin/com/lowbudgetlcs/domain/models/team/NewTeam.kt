package com.lowbudgetlcs.domain.models.team

import com.lowbudgetlcs.domain.models.events.EventId

data class NewTeam(
    val name: TeamName,
    val logoName: TeamLogoName? = null,
)

fun NewTeam.toTeam(
    id: TeamId,
    eventId: EventId? = null,
) = Team(
    id = id,
    logoName = logoName,
    name = name,
    eventId = eventId,
)
