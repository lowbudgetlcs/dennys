package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.NewPlayer
import com.lowbudgetlcs.domain.models.PlayerId
import com.lowbudgetlcs.domain.models.PlayerName
import com.lowbudgetlcs.domain.models.PlayerWithAccounts
import com.lowbudgetlcs.repositories.IPlayerRepository

class PlayerService(private val playerRepository: IPlayerRepository) {
    fun createPlayer(player: NewPlayer): PlayerWithAccounts? {
        if (isNameTaken(player.name.name)) return null
        return playerRepository.insert(player)
    }

    fun getPlayer(id: PlayerId): PlayerWithAccounts? {
        return playerRepository.getById(id)
    }

    fun getAllPlayers(): List<PlayerWithAccounts> = playerRepository.getAll()

    fun isNameTaken(name: String): Boolean {
        return playerRepository.getAll().any { it.name.name == name }
    }

    fun renamePlayer(playerId: PlayerId, newName: String): PlayerWithAccounts? {
        if (newName.isBlank() || isNameTaken(newName)) return null
        playerRepository.getById(playerId) ?: return null
        return playerRepository.renamePlayer(playerId, PlayerName(newName))
    }

}
