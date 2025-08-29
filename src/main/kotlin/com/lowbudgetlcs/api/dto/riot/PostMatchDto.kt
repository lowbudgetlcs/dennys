package com.lowbudgetlcs.api.dto.riot

import kotlinx.serialization.Serializable

@Serializable
data class PostMatchDto(
    val startTime: Long,
    val shortCode: String,
    val metaData: String,
    val gameId: Long = 0,
    val gameName: String = "",
    val gameType: String = "",
    val gameMap: Int = -1,
    val gameMode: String = "",
    val region: String = ""
)