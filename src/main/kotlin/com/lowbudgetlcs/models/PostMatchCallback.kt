package com.lowbudgetlcs.models

import kotlinx.serialization.Serializable

/**
 * Represents a callback that is recieved when a Tournament Match is completed.
 * [shortCode] and [gameId] are the most relevant fields.
 */
@Serializable
data class PostMatchCallback(
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