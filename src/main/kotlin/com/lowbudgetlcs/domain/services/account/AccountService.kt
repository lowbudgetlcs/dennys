package com.lowbudgetlcs.domain.services.account

import com.lowbudgetlcs.domain.models.player.account.Account
import com.lowbudgetlcs.domain.models.player.account.AccountId
import com.lowbudgetlcs.domain.models.player.account.NewAccount
import com.lowbudgetlcs.domain.models.player.account.Puuid
import com.lowbudgetlcs.gateways.riot.account.IRiotAccountGateway
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.account.IAccountRepository
import com.lowbudgetlcs.repositories.player.IPlayerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AccountService(
    private val accountRepository: IAccountRepository,
    private val playerRepository: IPlayerRepository,
    private val riotAccountGateway: IRiotAccountGateway,
) : IAccountService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun getAllAccounts(): List<Account> {
        logger.debug("Fetching all accounts...")
        return accountRepository.getAll()
    }

    override fun getAccount(accountId: AccountId): Account {
        logger.debug("Fetching account '$accountId'...")
        return accountRepository.getById(accountId) ?: throw NoSuchElementException("Account not found")
    }

    override suspend fun createAccount(account: NewAccount): Account {
        logger.debug("Creating new account...")
        logger.debug(account.toString())

        if (isPuuidTaken(account.puuid)) throw IllegalStateException("Account already exists.")

        // Call Riot API to verify PUUID
        riotAccountGateway.getAccountByPuuid(account.puuid) // throws if anything fails

        // Verify player ID
        account.playerId?.let { id ->
            playerRepository.getById(id) // throws if anything fails
        }

        return accountRepository.insert(account) ?: throw DatabaseException("Failed to insert account")
    }

    fun isPuuidTaken(puuid: Puuid): Boolean {
        logger.debug("Checking if '$puuid' is taken...")
        return accountRepository.getAccountByPuuid(puuid) != null
    }
}
