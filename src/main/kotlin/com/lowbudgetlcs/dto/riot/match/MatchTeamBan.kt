package com.lowbudgetlcs.dto.riot.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchTeamBan(
    @SerialName("championId") val championId: Int,
    @SerialName("pickTurn") val pickTurn: Int
)

