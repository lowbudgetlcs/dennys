package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.domain.models.riot.RiotAccountId
import com.lowbudgetlcs.repositories.IAccountRepository
import com.lowbudgetlcs.repositories.IPlayerRepository
import com.lowbudgetlcs.repositories.DatabaseException

class PlayerService(
    private val playerRepository: IPlayerRepository,
    private val accountRepository: IAccountRepository
) {

    fun createPlayer(player: NewPlayer): PlayerWithAccounts {
        if (player.name.value.isBlank()) throw IllegalArgumentException("Player name cannot be blank")
        if (isNameTaken(player.name.value)) throw IllegalStateException("Player name already exists")

        return playerRepository.insert(player)
            ?: throw DatabaseException("Failed to create player")
    }

    fun getPlayer(id: PlayerId): PlayerWithAccounts {
        return playerRepository.getById(id)
            ?: throw NoSuchElementException("Player not found")
    }

    fun getAllPlayers(): List<PlayerWithAccounts> = playerRepository.getAll()

    fun isNameTaken(name: String): Boolean {
        return playerRepository.getAll().any { it.name.value == name }
    }

    fun renamePlayer(playerId: PlayerId, newName: String): PlayerWithAccounts {
        if (newName.isBlank()) throw IllegalArgumentException("Player name cannot be blank")
        if (isNameTaken(newName)) throw IllegalStateException("Player name already exists")

        this.getPlayer(playerId) // throws if not found

        return playerRepository.renamePlayer(playerId, PlayerName(newName))
            ?: throw DatabaseException("Failed to rename player")
    }

    fun linkAccountToPlayer(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts {
        this.getPlayer(playerId) // throws if not found

        val account = accountRepository.getById(accountId)
            ?: throw NoSuchElementException("Account does not exist")

        if (account.playerId != null)
            throw IllegalStateException("Account already linked to another player")

        return playerRepository.insertAccountToPlayer(playerId, accountId)
            ?: throw DatabaseException("Failed to link account to player")
    }

    fun unlinkAccountFromPlayer(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts {
        val player = this.getPlayer(playerId) // throws if not found

        val account = accountRepository.getById(accountId)
            ?: throw NoSuchElementException("Account does not exist")

        if (player.accounts.none { it.id == account.id })
            throw IllegalStateException("Account does not belong to player")

        return playerRepository.removeAccount(playerId, accountId)
            ?: throw DatabaseException("Failed to unlink account from player")
    }

}
