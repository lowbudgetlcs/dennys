package com.lowbudgetlcs.entities.match

import com.lowbudgetlcs.util.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class LeagueOfLegendsMatchInfo(
    val endOfGameResult: String, // TODO: Make an Enum
    @Serializable(with = InstantSerializer::class) val gameCreation: Instant,
    val gameDuration: Int,
    @Serializable(with = InstantSerializer::class) val gameEndTimestamp: Instant,
    val gameId: Long,
    val gameMode: String, // TODO: Make an Enum
    val gameName: String,
    @Serializable(with = InstantSerializer::class) val gameStartTimestamp: Instant,
    val gameType: String, // TODO: Make an Enum
    val gameVersion: String,
    val mapId: Int, // TODO: Make an Enum
    // TODO: particpants implementation here
    val platformId: String, // TODO: Make an Enum
    val queueId: Int, // TODO: Make an Enum
    // TODO: teams implementation here
    val tournamentCode: String
)
