package com.lowbudgetlcs.domain.account.core

import com.lowbudgetlcs.domain.RepositoryException
import com.lowbudgetlcs.domain.account.core.model.Account
import com.lowbudgetlcs.domain.account.core.model.NewAccount
import com.lowbudgetlcs.domain.account.core.model.types.AccountId
import com.lowbudgetlcs.domain.account.core.model.types.Puuid
import com.lowbudgetlcs.domain.account.core.port.IAccountRepository
import com.lowbudgetlcs.domain.account.core.port.IAccountService
import com.lowbudgetlcs.domain.account.core.port.IRiotAccountGateway
import com.lowbudgetlcs.domain.player.core.port.IPlayerRepository
import com.lowbudgetlcs.logger

class AccountService(
    private val accountRepository: IAccountRepository,
    private val playerRepository: IPlayerRepository,
    private val riotAccountGateway: IRiotAccountGateway,
) : IAccountService {

    override suspend fun getAllAccounts(): List<Account> {
        logger.debug("Fetching all accounts...")
        return accountRepository.getAll()
    }

    override suspend fun getAccount(accountId: AccountId): Account {
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

        return accountRepository.insert(account) ?: throw RepositoryException("Failed to insert account")
    }

    suspend fun isPuuidTaken(puuid: Puuid): Boolean {
        logger.debug("Checking if '$puuid' is taken...")
        return accountRepository.getAccountByPuuid(puuid) != null
    }
}
