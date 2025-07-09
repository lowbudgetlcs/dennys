package com.lowbudgetlcs.dto.riot.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchTeamObjectivesDto(
    @SerialName("atakhan") val atakhan: MatchTeamObjectiveDto? = null,
    @SerialName("baron") val baron: MatchTeamObjectiveDto? = null,
    @SerialName("champion") val champion: MatchTeamObjectiveDto? = null,
    @SerialName("dragon") val dragon: MatchTeamObjectiveDto? = null,
    @SerialName("horde") val horde: MatchTeamObjectiveDto? = null,
    @SerialName("inhibitor") val inhibitor: MatchTeamObjectiveDto? = null,
    @SerialName("riftHerald") val riftHerald: MatchTeamObjectiveDto? = null,
    @SerialName("tower") val tower: MatchTeamObjectiveDto? = null
)
