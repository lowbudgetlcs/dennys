package com.lowbudgetlcs.repositories.player

import com.lowbudgetlcs.domain.models.NewPlayer
import com.lowbudgetlcs.domain.models.PlayerId
import com.lowbudgetlcs.domain.models.PlayerName
import com.lowbudgetlcs.domain.models.PlayerWithAccounts
import com.lowbudgetlcs.domain.models.riot.RiotAccountId

interface IPlayerRepository {
    fun insert(newPlayer: NewPlayer): PlayerWithAccounts?
    fun getAll(): List<PlayerWithAccounts>
    fun getById(id: PlayerId): PlayerWithAccounts?
    fun renamePlayer(id: PlayerId, newName: PlayerName): PlayerWithAccounts?

    fun insertAccountToPlayer(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts?
    fun removeAccount(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts?
}