package com.lowbudgetlcs.repositories.inmemory

import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.repositories.IPlayerRepository

class InMemoryPlayerRepository : IPlayerRepository {
    private val players = mutableListOf<PlayerWithAccounts>()
    private var currentPlayerId = 0

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

    fun clear() {
        players.clear()
        currentPlayerId = 0
    }
}

