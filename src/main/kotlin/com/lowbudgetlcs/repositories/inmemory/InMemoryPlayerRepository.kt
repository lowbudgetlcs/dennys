package com.lowbudgetlcs.repositories.inmemory

import com.lowbudgetlcs.domain.models.NewPlayer
import com.lowbudgetlcs.domain.models.PlayerId
import com.lowbudgetlcs.domain.models.PlayerName
import com.lowbudgetlcs.domain.models.PlayerWithAccounts
import com.lowbudgetlcs.repositories.IPlayerRepository

class InMemoryPlayerRepository : IPlayerRepository {
    private val players = mutableListOf<PlayerWithAccounts>()
    private var currentId = 0

    override fun insert(newPlayer: NewPlayer): PlayerWithAccounts? {
        val id = currentId++
        val player = PlayerWithAccounts(
            id = PlayerId(id),
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

    fun clear() = players.clear()
}

