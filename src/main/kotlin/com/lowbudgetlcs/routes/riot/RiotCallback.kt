package com.lowbudgetlcs.routes.riot

import kotlinx.serialization.Serializable

/**
 * Represents a callback that is recieved when a Tournament Match is completed.
 * [shortCode] and [gameId] are the most relevant fields.
 */
@Serializable
data class RiotCallback(
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