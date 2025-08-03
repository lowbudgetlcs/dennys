package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.*

interface IPlayerRepository {
    fun insert(newPlayer: NewPlayer): PlayerWithAccounts?
    fun getAll(): List<PlayerWithAccounts>
    fun getById(id: PlayerId): PlayerWithAccounts?
    fun renamePlayer(id: PlayerId, newName: PlayerName): PlayerWithAccounts?
}