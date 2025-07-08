package com.lowbudgetlcs.models.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchTeamObjective(
    @SerialName("first") val firstTaken: Boolean,
    @SerialName("kills") val kills: Int
)

