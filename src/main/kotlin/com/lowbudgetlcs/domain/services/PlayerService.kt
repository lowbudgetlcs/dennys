package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.repositories.IAccountRepository
import com.lowbudgetlcs.repositories.IPlayerRepository

class PlayerService(
    private val playerRepository: IPlayerRepository,
    private val accountRepository: IAccountRepository
) {

    fun createPlayer(player: NewPlayer): PlayerWithAccounts {
        if (player.name.name.isBlank()) throw IllegalArgumentException("Player name cannot be blank")
        if (isNameTaken(player.name.name)) throw IllegalStateException("Player name already exists")

        return playerRepository.insert(player)
            ?: throw IllegalStateException("Failed to create player")
    }

    fun getPlayer(id: PlayerId): PlayerWithAccounts {
        return playerRepository.getById(id)
            ?: throw NoSuchElementException("Player not found")
    }

    fun getAllPlayers(): List<PlayerWithAccounts> = playerRepository.getAll()

    fun isNameTaken(name: String): Boolean {
        return playerRepository.getAll().any { it.name.name == name }
    }

    fun renamePlayer(playerId: PlayerId, newName: String): PlayerWithAccounts {
        if (newName.isBlank()) throw IllegalArgumentException("Player name cannot be blank")
        if (isNameTaken(newName)) throw IllegalStateException("Player name already exists")

        getPlayer(playerId) // throws if not found

        return playerRepository.renamePlayer(playerId, PlayerName(newName))
            ?: throw IllegalStateException("Failed to rename player")
    }

    fun linkAccountToPlayer(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts {
        getPlayer(playerId) // throws if not found

        val account = accountRepository.getById(accountId)
            ?: throw NoSuchElementException("Account does not exist")

        if (account.playerId != null)
            throw IllegalStateException("Account already linked to another player")

        return playerRepository.insertAccountToPlayer(playerId, accountId)
            ?: throw IllegalStateException("Failed to link account to player")
    }

    fun unlinkAccountFromPlayer(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts {
        return playerRepository.removeAccount(playerId, accountId)
            ?: throw NoSuchElementException("Player or account not found")
    }

}
