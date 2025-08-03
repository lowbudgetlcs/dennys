package com.lowbudgetlcs.routes.dto.players

import kotlinx.serialization.Serializable

@Serializable
data class AddAccountToPlayerDto(
    val riotPuuid: String
)