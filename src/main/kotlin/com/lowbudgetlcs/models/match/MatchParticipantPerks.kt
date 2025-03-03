package com.lowbudgetlcs.models.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchParticipantPerks(
    @SerialName("styles") val perkStyles: List<MatchPerkStyle>
)
