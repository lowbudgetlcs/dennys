package com.lowbudgetlcs.repositories.players

import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.models.Player
import com.lowbudgetlcs.models.PlayerGameData
import com.lowbudgetlcs.models.PlayerId
import com.lowbudgetlcs.repositories.Repository
import kotlinx.serialization.Serializable

@Serializable
data class PlayerPerformanceId(val id: Int)

interface PlayerRepository : Repository<Player, PlayerId> {
    fun createPlayerData(
        player: Player, game: Game, data: PlayerGameData
    ): Player

    fun readByPuuid(puuid: String): Player?
}