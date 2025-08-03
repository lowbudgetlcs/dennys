package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.*
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

    fun linkAccountToPlayer(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts? {
        val player = playerRepository.getById(playerId) ?: return null

        // Account cannot be re-linked without being removed first
        val allPlayers = playerRepository.getAll()
        if (allPlayers.any { it.accounts.any { acc -> acc.id == accountId && it.id != playerId } }) {
            throw IllegalStateException("Account already linked to another player")
        }

        return playerRepository.insertAccountToPlayer(playerId, accountId)
    }

    fun unlinkAccountFromPlayer(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts? {
        return playerRepository.removeAccount(playerId, accountId)
    }

}
