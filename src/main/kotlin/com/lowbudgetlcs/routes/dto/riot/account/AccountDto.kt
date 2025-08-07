package com.lowbudgetlcs.routes.dto.riot.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    @SerialName("puuid") val puuid: String,
    @SerialName("gameName") val gameName: String? = null,
    @SerialName("tagLine") val tagLine: String? = null
)
