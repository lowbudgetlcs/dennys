@file:UseSerializers(InstantSerializer::class)

package com.lowbudgetlcs.routes.dto.riot.match

import com.lowbudgetlcs.routes.dto.InstantSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Instant

@Serializable
data class MatchInfoDto(
    val endOfGameResult: String, // TODO: Make an Enum
    val gameCreation: Instant,
    val gameDuration: Int,
    val gameEndTimestamp: Instant,
    val gameId: Long,
    val gameMode: String, // TODO: Make an Enum
    val gameName: String,
    val gameStartTimestamp: Instant,
    val gameType: String, // TODO: Make an Enum
    val gameVersion: String,
    val mapId: Int, // TODO: Make an Enum
    val participants: List<MatchParticipantDto>,
    val platformId: String, // TODO: Make an Enum
    val queueId: Int, // TODO: Make an Enum
    val teams: List<MatchTeamDto>,
    val tournamentCode: String
)
