package com.lowbudgetlcs.routes.dto.riot.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchTeamDto(
    @SerialName("teamId") val teamId: Int,
    @SerialName("win") val win: Boolean,
    @SerialName("bans") val bans: List<MatchTeamBan>,
    @SerialName("objectives") val objectives: MatchTeamObjectivesDto
)

