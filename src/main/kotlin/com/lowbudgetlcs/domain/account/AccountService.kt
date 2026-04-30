package com.lowbudgetlcs.domain.account

import com.lowbudgetlcs.domain.account.models.Account
import com.lowbudgetlcs.domain.account.models.NewAccount
import com.lowbudgetlcs.domain.account.models.types.AccountId
import com.lowbudgetlcs.domain.account.models.types.Puuid
import com.lowbudgetlcs.domain.account.repositories.IAccountRepository
import com.lowbudgetlcs.gateways.riot.account.IRiotAccountGateway
import com.lowbudgetlcs.repositories.DatabaseException
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

        check(!isPuuidTaken(account.puuid)) { "Account already exists." }

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
