package com.lowbudgetlcs.dto.riot.match

import com.lowbudgetlcs.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class MatchInfoDto(
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
    val participants: List<MatchParticipantDto>,
    val platformId: String, // TODO: Make an Enum
    val queueId: Int, // TODO: Make an Enum
    val teams: List<MatchTeamDto>,
    val tournamentCode: String
)
