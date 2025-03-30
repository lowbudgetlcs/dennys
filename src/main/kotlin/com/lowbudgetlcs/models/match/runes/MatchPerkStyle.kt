package com.lowbudgetlcs.models.match.runes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchPerkStyle(
    @SerialName("description") val description: String,
    @SerialName("selections") val selections: List<PerkStyleSelection>,
    @SerialName("style") val style: Int
)
