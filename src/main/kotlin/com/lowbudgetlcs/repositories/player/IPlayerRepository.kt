package com.lowbudgetlcs.repositories.player

import com.lowbudgetlcs.domain.models.player.NewPlayer
import com.lowbudgetlcs.domain.models.player.Player
import com.lowbudgetlcs.domain.models.player.PlayerId
import com.lowbudgetlcs.domain.models.player.PlayerName
import com.lowbudgetlcs.domain.models.team.TeamId

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
