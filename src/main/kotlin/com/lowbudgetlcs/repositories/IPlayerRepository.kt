package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.NewPlayer
import com.lowbudgetlcs.domain.models.PlayerId
import com.lowbudgetlcs.domain.models.PlayerName
import com.lowbudgetlcs.domain.models.PlayerWithAccounts

interface IPlayerRepository {
    fun insert(newPlayer: NewPlayer): PlayerWithAccounts?
    fun getAll(): List<PlayerWithAccounts>
    fun getById(id: PlayerId): PlayerWithAccounts?
    fun renamePlayer(id: PlayerId, newName: PlayerName): PlayerWithAccounts?
}