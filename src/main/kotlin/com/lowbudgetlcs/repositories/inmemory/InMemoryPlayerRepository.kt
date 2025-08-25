package com.lowbudgetlcs.repositories.inmemory

import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.domain.models.riot.RiotAccount
import com.lowbudgetlcs.domain.models.riot.RiotAccountId
import com.lowbudgetlcs.domain.models.riot.RiotPuuid
import com.lowbudgetlcs.repositories.IPlayerRepository

class InMemoryPlayerRepository : IPlayerRepository {
    private val players = mutableListOf<PlayerWithAccounts>()
    private var currentPlayerId = 0

    private val accounts = mutableMapOf<RiotAccountId, RiotAccount>()
    private var currentAccountId = 0

    override fun insert(newPlayer: NewPlayer): PlayerWithAccounts {
        val id = PlayerId(currentPlayerId++)
        val player = PlayerWithAccounts(
            id = id,
            name = newPlayer.name,
            accounts = emptyList()
        )
        players.add(player)
        return player
    }

    override fun getAll(): List<PlayerWithAccounts> = players.toList()

    override fun getById(id: PlayerId): PlayerWithAccounts? = players.find { it.id == id }

    override fun renamePlayer(id: PlayerId, newName: PlayerName): PlayerWithAccounts? {
        val index = players.indexOfFirst { it.id == id }
        if (index == -1) return null

        val updated = players[index].copy(name = newName)
        players[index] = updated
        return updated
    }

    override fun insertAccountToPlayer(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts? {
        val playerIndex = players.indexOfFirst { it.id == playerId }
        if (playerIndex == -1) return null

        val account = accounts[accountId] ?: return null

        if (account.playerId != null && account.playerId != playerId) return null

        val updatedAccount = account.copy(playerId = playerId)
        accounts[accountId] = updatedAccount

        val player = players[playerIndex]
        val updatedPlayer = player.copy(accounts = player.accounts + updatedAccount)
        players[playerIndex] = updatedPlayer

        return updatedPlayer
    }

    override fun removeAccount(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts? {
        val playerIndex = players.indexOfFirst { it.id == playerId }
        if (playerIndex == -1) return null

        val account = accounts[accountId] ?: return null

        if (account.playerId != playerId) return null

        // Unlink account from player
        val updatedAccount = account.copy(playerId = null)
        accounts[accountId] = updatedAccount

        val player = players[playerIndex]
        val updatedPlayer = player.copy(
            accounts = player.accounts.filterNot { it.id == accountId }
        )
        players[playerIndex] = updatedPlayer

        return updatedPlayer
    }

    fun createAccountRecord(riotPuuid: RiotPuuid): RiotAccount {
        val id = RiotAccountId(currentAccountId++)
        val account = RiotAccount(id = id, riotPuuid = riotPuuid, playerId = null)
        accounts[id] = account
        return account
    }

    fun clear() {
        players.clear()
        currentPlayerId = 0
    }
}

