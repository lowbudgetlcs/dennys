package com.lowbudgetlcs.api.routes.v1.player.dto

import kotlinx.serialization.Serializable

@Serializable
data class PatchPlayerDto(
    val name: String,
)
