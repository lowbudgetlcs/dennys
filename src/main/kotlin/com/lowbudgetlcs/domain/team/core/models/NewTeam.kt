package com.lowbudgetlcs.domain.team.core.models

import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.team.core.models.types.TeamId
import com.lowbudgetlcs.domain.team.core.models.types.TeamName

data class NewTeam(
    val name: TeamName,
    val logo: String? = null,
)

// Extensions
fun NewTeam.toTeam(id: TeamId, eventId: EventId?): Team = Team(
    id = id, name = name, logo = logo, eventId = eventId
)
