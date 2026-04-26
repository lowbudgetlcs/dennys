package com.lowbudgetlcs.api.routes.v1.account.dto

import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    val id: Int,
    val riotPuuid: String,
    val playerId: Int?,
)
