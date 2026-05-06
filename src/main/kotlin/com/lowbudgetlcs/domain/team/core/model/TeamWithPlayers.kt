package com.lowbudgetlcs.domain.team.core.model

import com.lowbudgetlcs.domain.division.core.model.types.EventId
import com.lowbudgetlcs.domain.player.core.model.Player
import com.lowbudgetlcs.domain.team.core.model.types.TeamId
import com.lowbudgetlcs.domain.team.core.model.types.TeamName

data class TeamWithPlayers(
    val id: TeamId,
    val name: TeamName,
    val logo: String?,
    val eventId: EventId?,
    val players: List<Player>,
)
