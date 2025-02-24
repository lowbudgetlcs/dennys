package com.lowbudgetlcs.repositories.players

import com.lowbudgetlcs.entities.Game
import com.lowbudgetlcs.entities.Player
import com.lowbudgetlcs.entities.PlayerGameData
import com.lowbudgetlcs.entities.PlayerId
import com.lowbudgetlcs.repositories.IRepository
import kotlinx.serialization.Serializable

@Serializable
data class PlayerPerformanceId(val id: Int)

interface IPlayerRepository : IRepository<Player, PlayerId> {
    fun createPlayerData(
        player: Player, game: Game, data: PlayerGameData
    ): Player

    fun readByPuuid(puuid: String): Player?
}