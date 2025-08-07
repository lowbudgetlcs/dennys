package com.lowbudgetlcs.routes.dto.riot.match.perks

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchPerkStyle(
    @SerialName("description") val description: String,
    @SerialName("selections") val selections: List<PerkStyleSelection>,
    @SerialName("style") val style: Int
)
