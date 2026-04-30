package com.lowbudgetlcs.domain.player.core

import com.lowbudgetlcs.domain.account.core.model.types.AccountId
import com.lowbudgetlcs.domain.account.core.port.IAccountRepository
import com.lowbudgetlcs.domain.player.core.model.NewPlayer
import com.lowbudgetlcs.domain.player.core.model.Player
import com.lowbudgetlcs.domain.player.core.model.PlayerWithTeams
import com.lowbudgetlcs.domain.player.core.model.toPlayerWithTeams
import com.lowbudgetlcs.domain.player.core.model.types.PlayerId
import com.lowbudgetlcs.domain.player.core.model.types.PlayerName
import com.lowbudgetlcs.domain.player.core.port.IPlayerRepository
import com.lowbudgetlcs.domain.player.core.port.IPlayerService
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.domain.team.core.port.ITeamRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PlayerService(
    private val playerRepository: IPlayerRepository,
    private val accountRepository: IAccountRepository,
    private val teamRepository: ITeamRepository,
) : IPlayerService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun getAllPlayers(): List<Player> {
        logger.info("Fetching all players...")
        val players = playerRepository.getAll()
        logger.debug("Fetched ${players.size} players.")
        logger.debug(players.toString())
        return players
    }

    override fun getPlayer(id: PlayerId): Player {
        logger.info("Fetching player '$id'...")
        val player = playerRepository.getById(id) ?: throw NoSuchElementException("Player not found")
        logger.debug("Fetched: {}.", player)
        return player
    }

    override fun getPlayerWithTeams(id: PlayerId): PlayerWithTeams {
        logger.info("Fetching player '$id' with teams...")
        val player = getPlayer(id)
        val teams = teamRepository.getByPlayerId(player.id)
        logger.debug("Fetched ${teams.size} teams.")
        logger.debug(teams.toString())
        return player.toPlayerWithTeams(player, teams)
    }

    override fun createPlayer(player: NewPlayer): Player {
        logger.info("Creating new player...")
        logger.debug(player.toString())
        require(player.name.value.isNotBlank()) { "Player name cannot be blank" }
        check(!isNameTaken(player.name)) { "Player name already exists" }
        val player = playerRepository.insert(player) ?: throw DatabaseException("Failed to create player")
        logger.debug("Created: {}", player)
        return player
    }

    override fun renamePlayer(
        playerId: PlayerId,
        newName: PlayerName,
    ): Player {
        logger.info("Renaming player '$playerId' to '$newName'...")
        check(!isNameTaken(newName)) { "Player named ${newName.value} already exists" }
        this.getPlayer(playerId) // throws if not found
        val player = playerRepository.renamePlayer(playerId, newName) ?: throw DatabaseException("Failed to rename player")
        logger.debug("Renamed: {}", player)
        return player
    }

    // TODO: Extract some of this logic into separate functions
    override fun linkAccountToPlayer(
        playerId: PlayerId,
        accountId: AccountId,
    ): Player {
        logger.info("Linking account '$accountId' to player '$playerId'...")
        this.getPlayer(playerId) // throws if not found
        val account = accountRepository.getById(accountId) ?: throw NoSuchElementException("Account does not exist")
        checkNotNull(account.playerId) { "Account already owned." }
        accountRepository.updatePlayerId(accountId, playerId)
            ?: throw DatabaseException("Failed to link account to player.")
        val player = playerRepository.getById(playerId) ?: throw NoSuchElementException("Player not found.")
        logger.debug("Linked: {}", player)
        return player
    }

    override fun unlinkAccountFromPlayer(
        playerId: PlayerId,
        accountId: AccountId,
    ): Player {
        logger.info("Removing account '$accountId' from player '$playerId'...")
        val player = this.getPlayer(playerId) // throws if not found
        val account = accountRepository.getById(accountId) ?: throw NoSuchElementException("Account not found.")
        logger.debug("Account: {}", account)
        check(account.playerId == player.id) { "Account belongs to different player." }
        accountRepository.updatePlayerId(accountId, null) ?: throw DatabaseException("Failed to remove account.")
        return playerRepository.getById(playerId) ?: throw NoSuchElementException("Player not found.")
    }

    /**
     * @param name The PlayerName to check.
     * @return True if the name is taken, false otherwise.
     */
    fun isNameTaken(name: PlayerName): Boolean {
        logger.debug("Checking if name '$name' is taken...")
        return playerRepository.getByName(name) != null
    }
}
