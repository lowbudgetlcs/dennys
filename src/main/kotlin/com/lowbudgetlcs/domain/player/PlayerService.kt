package com.lowbudgetlcs.domain.player

import com.lowbudgetlcs.domain.account.models.types.AccountId
import com.lowbudgetlcs.domain.player.models.NewPlayer
import com.lowbudgetlcs.domain.player.models.Player
import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.domain.player.models.types.PlayerName
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.account.IAccountRepository
import com.lowbudgetlcs.repositories.player.IPlayerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PlayerService(
    private val playerRepository: IPlayerRepository,
    private val accountRepository: IAccountRepository,
) : IPlayerService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun getAllPlayers(): List<Player> {
        logger.debug("Fetching all players...")
        return playerRepository.getAll()
    }

    override fun getPlayer(id: PlayerId): Player {
        logger.debug("Fetching player '$id'...")
        return playerRepository.getById(id) ?: throw NoSuchElementException("Player not found")
    }

    override fun createPlayer(player: NewPlayer): Player {
        logger.debug("Creating new player...")
        logger.debug(player.toString())
        require(player.name.value.isNotBlank()) { "Player name cannot be blank" }
        check(!isNameTaken(player.name)) { "Player name already exists" }
        return playerRepository.insert(player) ?: throw DatabaseException("Failed to create player")
    }

    override fun renamePlayer(
        playerId: PlayerId,
        newName: PlayerName,
    ): Player {
        logger.debug("Renaming player '$playerId' to '$newName'...")
        check(!isNameTaken(newName)) { "Player named ${newName.value} already exists" }

        this.getPlayer(playerId) // throws if not found

        return playerRepository.renamePlayer(playerId, newName) ?: throw DatabaseException("Failed to rename player")
    }

    override fun linkAccountToPlayer(
        playerId: PlayerId,
        accountId: AccountId,
    ): Player {
        logger.debug("Linking account '$accountId' to player '$playerId'...")
        this.getPlayer(playerId) // throws if not found

        val account = accountRepository.getById(accountId) ?: throw NoSuchElementException("Account does not exist")

        checkNotNull(account.playerId) { "Account already owned." }

        accountRepository.updatePlayerId(accountId, playerId)
            ?: throw DatabaseException("Failed to link account to player.")
        return playerRepository.getById(playerId) ?: throw NoSuchElementException("Player not found.")
    }

    override fun unlinkAccountFromPlayer(
        playerId: PlayerId,
        accountId: AccountId,
    ): Player {
        logger.debug("Removing account '$accountId' from player '$playerId'...")
        val player = this.getPlayer(playerId) // throws if not found

        val account = accountRepository.getById(accountId) ?: throw NoSuchElementException("Account not found.")

        check(account.playerId == player.id) { "Account belongs to different player." }

        accountRepository.updatePlayerId(accountId, null) ?: throw DatabaseException("Failed to remove account.")
        return playerRepository.getById(playerId) ?: throw NoSuchElementException("Player not found.")
    }

    fun isNameTaken(name: PlayerName): Boolean {
        logger.debug("Checking if name '$name' is taken...")
        return playerRepository.getAll().any { it.name == name }
    }
}
