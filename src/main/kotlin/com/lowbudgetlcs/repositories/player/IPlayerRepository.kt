package com.lowbudgetlcs.repositories.player

import com.lowbudgetlcs.domain.player.models.NewPlayer
import com.lowbudgetlcs.domain.player.models.Player
import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.domain.player.models.types.PlayerName
import com.lowbudgetlcs.domain.team.models.types.TeamId

interface IPlayerRepository {
    fun getAll(): List<Player>

    fun getById(id: PlayerId): Player?

    fun getByTeamId(teamId: TeamId): List<Player>

    fun insert(newPlayer: NewPlayer): Player?

    fun renamePlayer(
        id: PlayerId,
        newName: PlayerName,
    ): Player?
}
