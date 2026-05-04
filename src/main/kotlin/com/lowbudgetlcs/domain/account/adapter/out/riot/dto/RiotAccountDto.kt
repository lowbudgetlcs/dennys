package com.lowbudgetlcs.domain.account.adapter.out.riot.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RiotAccountDto(
    @SerialName("puuid") val puuid: String,
    @SerialName("gameName") val gameName: String? = null,
    @SerialName("tagLine") val tagLine: String? = null,
)
