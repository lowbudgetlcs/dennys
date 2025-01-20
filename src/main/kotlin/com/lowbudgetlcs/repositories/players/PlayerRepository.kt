package com.lowbudgetlcs.repositories.players

import com.lowbudgetlcs.models.*
import com.lowbudgetlcs.repositories.Repository
import kotlinx.serialization.Serializable

@Serializable
data class PlayerPerformanceId(val id: Int)

@Serializable
data class PlayerPerformance(
    val gameId: GameId, val teamId: TeamId, val divisionId: DivisionId
)

interface PlayerRepository : Repository<Player, PlayerId> {
    fun readByPuuid(puuid: String): Player?
    fun createPlayerData(
        player: Player, game: Game, data: PlayerGameData
    ): Player
}