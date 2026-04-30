package com.lowbudgetlcs.domain.team.models

import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.team.models.types.TeamId
import com.lowbudgetlcs.domain.team.models.types.TeamName

data class Team(
    val id: TeamId,
    val name: TeamName,
    val logo: String?,
    val eventId: EventId?,
)
