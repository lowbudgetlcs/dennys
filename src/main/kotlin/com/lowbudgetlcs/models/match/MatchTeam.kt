package com.lowbudgetlcs.models.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchTeam(
    @SerialName("teamId") val teamId: Int,
    @SerialName("win") val win: Boolean,
    @SerialName("bans") val bans: List<MatchTeamBan>,
    @SerialName("objectives") val objectives: MatchTeamObjectives
)

