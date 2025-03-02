package com.lowbudgetlcs.models.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchTeamObjectives(
    @SerialName("atakhan") val atakhan: MatchTeamObjective,
    @SerialName("baron") val baron: MatchTeamObjective,
    @SerialName("champion") val champion: MatchTeamObjective,
    @SerialName("dragon") val dragon: MatchTeamObjective,
    @SerialName("horde") val horde: MatchTeamObjective,
    @SerialName("inhibitor") val inhibitor: MatchTeamObjective,
    @SerialName("riftHerald") val riftHerald: MatchTeamObjective,
    @SerialName("tower") val tower: MatchTeamObjective
)

