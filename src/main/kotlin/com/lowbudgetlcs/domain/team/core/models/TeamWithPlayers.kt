package com.lowbudgetlcs.domain.team.core.models

import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.player.core.model.Player
import com.lowbudgetlcs.domain.team.core.models.types.TeamId
import com.lowbudgetlcs.domain.team.core.models.types.TeamName

data class TeamWithPlayers(
    val id: TeamId,
    val name: TeamName,
    val logo: String?,
    val eventId: EventId?,
    val players: List<Player>,
)
