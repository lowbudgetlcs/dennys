package com.lowbudgetlcs.domain.services.account

import com.lowbudgetlcs.domain.models.riot.account.NewRiotAccount
import com.lowbudgetlcs.domain.models.riot.account.RiotAccount
import com.lowbudgetlcs.domain.models.riot.account.RiotAccountId
import com.lowbudgetlcs.domain.models.riot.account.RiotPuuid
import com.lowbudgetlcs.gateways.riot.account.IRiotAccountGateway
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.account.IAccountRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AccountService(
    private val accountRepository: IAccountRepository, private val riotAccountGateway: IRiotAccountGateway
) : IAccountService {
    private val logger: Logger by lazy { LoggerFactory.getLogger(this::class.java) }

    override fun getAccount(accountId: RiotAccountId): RiotAccount {
        logger.debug("Fetching account '$accountId'...")
        return accountRepository.getById(accountId) ?: throw NoSuchElementException("Account not found")
    }

    override fun getAllAccounts(): List<RiotAccount> {
        logger.debug("Fetching all accounts...")
        return accountRepository.getAll()
    }

    override suspend fun createAccount(account: NewRiotAccount): RiotAccount {
        logger.debug("Creating new account...")
        logger.debug(account.toString())
        val puuid = account.riotPuuid

        if (isPuuidTaken(puuid)) throw IllegalStateException("Riot account already exists")

        // Call Riot API to verify PUUID
        riotAccountGateway.getAccountByPuuid(puuid.value) // throws if anything fails

        return accountRepository.insert(account) ?: throw DatabaseException("Failed to insert account")
    }

    fun isPuuidTaken(puuid: RiotPuuid): Boolean {
        logger.debug("Checking if '$puuid' is taken...")
        return accountRepository.getAccountByPuuid(puuid.value) != null
    }

}