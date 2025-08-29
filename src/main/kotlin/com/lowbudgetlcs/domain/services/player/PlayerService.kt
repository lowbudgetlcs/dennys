package com.lowbudgetlcs.domain.services.player

import com.lowbudgetlcs.domain.models.NewPlayer
import com.lowbudgetlcs.domain.models.PlayerId
import com.lowbudgetlcs.domain.models.PlayerName
import com.lowbudgetlcs.domain.models.PlayerWithAccounts
import com.lowbudgetlcs.domain.models.riot.RiotAccountId
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.account.IAccountRepository
import com.lowbudgetlcs.repositories.player.IPlayerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PlayerService(
    private val playerRepository: IPlayerRepository, private val accountRepository: IAccountRepository
) : IPlayerService {

    private val logger: Logger by lazy { LoggerFactory.getLogger(this::class.java) }
    override fun getAllPlayers(): List<PlayerWithAccounts> {
        logger.debug("Fetching all players...")
        return playerRepository.getAll()
    }

    override fun getPlayer(id: PlayerId): PlayerWithAccounts {
        logger.debug("Fetching player '$id'...")
        return playerRepository.getById(id) ?: throw NoSuchElementException("Player not found")
    }

    override fun createPlayer(player: NewPlayer): PlayerWithAccounts {
        logger.debug("Creating new player...")
        logger.debug(player.toString())
        if (player.name.value.isBlank()) throw IllegalArgumentException("Player name cannot be blank")
        if (isNameTaken(player.name.value)) throw IllegalStateException("Player name already exists")

        return playerRepository.insert(player) ?: throw DatabaseException("Failed to create player")
    }

    override fun renamePlayer(playerId: PlayerId, newName: String): PlayerWithAccounts {
        logger.debug("Renaming player '$playerId' to '$newName'...")
        if (newName.isBlank()) throw IllegalArgumentException("Player name cannot be blank")
        if (isNameTaken(newName)) throw IllegalStateException("Player name already exists")

        this.getPlayer(playerId) // throws if not found

        return playerRepository.renamePlayer(playerId, PlayerName(newName))
            ?: throw DatabaseException("Failed to rename player")
    }

    override fun linkAccountToPlayer(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts {
        logger.debug("Linking account '$accountId' to player '$playerId'...")
        this.getPlayer(playerId) // throws if not found

        val account = accountRepository.getById(accountId) ?: throw NoSuchElementException("Account does not exist")

        if (account.playerId != null) throw IllegalStateException("Account already linked to another player")

        return playerRepository.insertAccountToPlayer(playerId, accountId)
            ?: throw DatabaseException("Failed to link account to player")
    }

    override fun unlinkAccountFromPlayer(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts {
        logger.debug("Removing account '$accountId' from player '$playerId'...")
        val player = this.getPlayer(playerId) // throws if not found

        val account = accountRepository.getById(accountId) ?: throw NoSuchElementException("Account does not exist")

        if (player.accounts.none { it.id == account.id }) throw IllegalStateException("Account does not belong to player")

        return playerRepository.removeAccount(playerId, accountId)
            ?: throw DatabaseException("Failed to unlink account from player")
    }

    fun isNameTaken(name: String): Boolean {
        logger.debug("Checking if name '$name' is taken...")
        return playerRepository.getAll().any { it.name.value == name }
    }
}