package com.lowbudgetlcs.domain.team.models

import com.lowbudgetlcs.domain.event.models.EventId
import com.lowbudgetlcs.domain.player.models.Player
import com.lowbudgetlcs.domain.team.models.types.TeamId
import com.lowbudgetlcs.domain.team.models.types.TeamName

data class TeamWithPlayers(
    val id: TeamId,
    val name: TeamName,
    val logo: String?,
    val eventId: EventId?,
    val players: List<Player>,
)
