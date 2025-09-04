package com.lowbudgetlcs.api.dto.accounts

import kotlinx.serialization.Serializable

@Serializable
data class NewAccountDto(
    val riotPuuid: String
)
