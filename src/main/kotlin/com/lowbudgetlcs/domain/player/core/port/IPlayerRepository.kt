package com.lowbudgetlcs.domain.player.core.port

import com.lowbudgetlcs.domain.player.core.model.NewPlayer
import com.lowbudgetlcs.domain.player.core.model.Player
import com.lowbudgetlcs.domain.player.core.model.types.PlayerId
import com.lowbudgetlcs.domain.player.core.model.types.PlayerName
import com.lowbudgetlcs.domain.team.core.models.types.TeamId

interface IPlayerRepository {
    fun getAll(): List<Player>

    fun getById(id: PlayerId): Player?

    fun getByName(playerName: PlayerName): Player?

    fun getByTeamId(teamId: TeamId): List<Player>

    fun insert(newPlayer: NewPlayer): Player?

    fun renamePlayer(
        id: PlayerId,
        newName: PlayerName,
    ): Player?
}
