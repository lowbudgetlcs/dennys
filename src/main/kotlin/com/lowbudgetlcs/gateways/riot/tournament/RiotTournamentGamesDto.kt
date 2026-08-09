package com.lowbudgetlcs.gateways.riot.tournament

import kotlinx.serialization.Serializable

@Serializable
data class RiotTournamentTeamV5Dto(
    val puuid: String = "",
)

@Serializable
data class RiotTournamentGamesV5Dto(
    val startTime: Long = 0,
    val winningTeam: List<RiotTournamentTeamV5Dto> = emptyList(),
    val losingTeam: List<RiotTournamentTeamV5Dto> = emptyList(),
    val shortCode: String = "",
    val metaData: String = "",
    val gameId: Long = 0,
    val gameName: String = "",
    val gameType: String = "",
    val gameMap: Int = -1,
    val gameMode: String = "",
    val region: String = "",
)
