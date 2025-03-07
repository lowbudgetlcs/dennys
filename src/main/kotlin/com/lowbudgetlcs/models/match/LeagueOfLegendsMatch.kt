package com.lowbudgetlcs.models.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeagueOfLegendsMatch(
    @SerialName("metadata") val metaData: LeagueOfLegendsMatchMetaData,
    @SerialName("info") val matchInfo: LeagueOfLegendsMatchInfo
)
