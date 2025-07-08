package com.lowbudgetlcs.dto.lol.match.perks

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchParticipantPerks(
    @SerialName("statPerks") val statPerks: PerkStats,
    @SerialName("styles") val perkStyles: List<MatchPerkStyle>
)
