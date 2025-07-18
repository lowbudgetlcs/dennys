package com.lowbudgetlcs.models.match.runes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PerkStyleSelection(
    @SerialName("perk") val perkId: Int,
    @SerialName("var1") val var1: Int,
    @SerialName("var2") val var2: Int,
    @SerialName("var3") val var3: Int
)