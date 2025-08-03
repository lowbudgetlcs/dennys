package com.lowbudgetlcs.repositories.inmemory

import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.repositories.IPlayerRepository

class InMemoryPlayerRepository : IPlayerRepository {
    private val players = mutableListOf<PlayerWithAccounts>()
    private val accounts = mutableMapOf<RiotAccountId, RiotAccount>()
    private var currentPlayerId = 0
    private var currentAccountId = 0

    override fun insert(newPlayer: NewPlayer): PlayerWithAccounts? {
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

    override fun createAccountRecord(riotPuuid: RiotPuuid): RiotAccount {
        val accountId = RiotAccountId(currentAccountId++)
        val account = RiotAccount(
            id = accountId,
            riotPuuid = riotPuuid,
            playerId = null
        )
        accounts[accountId] = account
        return account
    }

    override fun insertAccountToPlayer(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts? {
        val playerIndex = players.indexOfFirst { it.id == playerId }
        val account = accounts[accountId] ?: return null
        if (playerIndex == -1) return null

        val updatedAccount = account.copy(playerId = playerId)
        accounts[accountId] = updatedAccount

        val player = players[playerIndex]
        val updatedPlayer = player.copy(accounts = player.accounts + updatedAccount)
        players[playerIndex] = updatedPlayer

        return updatedPlayer
    }

    override fun removeAccount(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts? {
        val index = players.indexOfFirst { it.id == playerId }
        if (index == -1) return null

        val player = players[index]
        val updatedAccounts = player.accounts.filterNot { it.id == accountId }

        accounts.remove(accountId)
        val updatedPlayer = player.copy(accounts = updatedAccounts)
        players[index] = updatedPlayer

        return updatedPlayer
    }

    fun clear() {
        players.clear()
        accounts.clear()
        currentPlayerId = 0
        currentAccountId = 0
    }
}

