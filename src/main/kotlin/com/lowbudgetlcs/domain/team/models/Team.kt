package com.lowbudgetlcs.domain.team.models

import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.team.models.types.TeamId
import com.lowbudgetlcs.domain.team.models.types.TeamLogoName
import com.lowbudgetlcs.domain.team.models.types.TeamName

data class Team(
    val id: TeamId,
    val name: TeamName,
    val logoName: String?,
    val eventId: EventId?,
)
