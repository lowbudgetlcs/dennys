package com.lowbudgetlcs.api.routes.v1.account.dto

import kotlinx.serialization.Serializable

@Serializable
data class NewAccountDto(
    val riotPuuid: String,
)
