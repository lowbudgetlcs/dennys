package com.lowbudgetlcs.models.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchTeamObjectives(
    @SerialName("atakhan") val atakhan: MatchTeamObjective? = null,
    @SerialName("baron") val baron: MatchTeamObjective? = null,
    @SerialName("champion") val champion: MatchTeamObjective? = null,
    @SerialName("dragon") val dragon: MatchTeamObjective? = null,
    @SerialName("horde") val horde: MatchTeamObjective? = null,
    @SerialName("inhibitor") val inhibitor: MatchTeamObjective? = null,
    @SerialName("riftHerald") val riftHerald: MatchTeamObjective? = null,
    @SerialName("tower") val tower: MatchTeamObjective? = null
)
