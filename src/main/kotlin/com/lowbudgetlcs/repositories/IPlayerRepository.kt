package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.domain.models.riot.RiotAccountId

interface IPlayerRepository {
    fun insert(newPlayer: NewPlayer): PlayerWithAccounts?
    fun getAll(): List<PlayerWithAccounts>
    fun getById(id: PlayerId): PlayerWithAccounts?
    fun renamePlayer(id: PlayerId, newName: PlayerName): PlayerWithAccounts?

    fun insertAccountToPlayer(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts?
    fun removeAccount(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts?
}