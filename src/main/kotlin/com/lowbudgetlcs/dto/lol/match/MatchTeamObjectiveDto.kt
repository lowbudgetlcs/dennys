package com.lowbudgetlcs.dto.lol.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchTeamObjectiveDto(
    @SerialName("first") val firstTaken: Boolean,
    @SerialName("kills") val kills: Int
)

