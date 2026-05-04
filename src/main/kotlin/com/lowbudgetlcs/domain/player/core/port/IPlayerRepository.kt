package com.lowbudgetlcs.domain.player.core.port

import com.lowbudgetlcs.domain.player.core.model.NewPlayer
import com.lowbudgetlcs.domain.player.core.model.Player
import com.lowbudgetlcs.domain.player.core.model.types.PlayerId
import com.lowbudgetlcs.domain.player.core.model.types.PlayerName
import com.lowbudgetlcs.domain.team.core.model.types.TeamId

interface IPlayerRepository {
    suspend fun getAll(): List<Player>
    suspend fun getById(id: PlayerId): Player?
    suspend fun getByName(playerName: PlayerName): Player?
    suspend fun getByTeamId(teamId: TeamId): List<Player>
    suspend fun insert(newPlayer: NewPlayer): Player?
    suspend fun renamePlayer(
        id: PlayerId,
        newName: PlayerName,
    ): Player?
}
