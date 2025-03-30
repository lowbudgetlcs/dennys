package com.lowbudgetlcs.models.match.runes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PerkStats(
    @SerialName("defense") val defense: Int,
    @SerialName("flex") val flex: Int,
    @SerialName("offense") val offense: Int
)
