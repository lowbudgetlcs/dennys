package com.lowbudgetlcs.repositories.players

import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.models.Player
import com.lowbudgetlcs.models.PlayerGameData
import com.lowbudgetlcs.models.PlayerId
import com.lowbudgetlcs.repositories.Repository
import kotlinx.serialization.Serializable
import migrations.Player_game_data
import migrations.Players

@Serializable
data class PlayerPerformanceId(val id: Int)

interface PlayerRepository : Repository<Player, PlayerId> {
    fun readByPuuid(puuid: String): Player?
    fun createPlayerData(
        player: Player, game: Game, data: PlayerGameData
    ): Player

    fun Players.toPlayer(): Player
    fun Player_game_data.toPlayerGameData(): PlayerGameData
}