package com.lowbudgetlcs.domain.services.account

import com.lowbudgetlcs.domain.models.riot.NewRiotAccount
import com.lowbudgetlcs.domain.models.riot.RiotAccount
import com.lowbudgetlcs.domain.models.riot.RiotAccountId
import com.lowbudgetlcs.domain.models.riot.RiotPuuid
import com.lowbudgetlcs.gateways.riot.account.IRiotAccountGateway
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.account.IAccountRepository

class AccountService(
    private val accountRepository: IAccountRepository, private val riotAccountGateway: IRiotAccountGateway
) : IAccountService {

    override fun getAccount(accountId: RiotAccountId): RiotAccount {
        return accountRepository.getById(accountId) ?: throw NoSuchElementException("Account not found")
    }

    override fun getAllAccounts(): List<RiotAccount> = accountRepository.getAll()

    override suspend fun createAccount(account: NewRiotAccount): RiotAccount {
        val puuid = account.riotPuuid

        if (isPuuidTaken(puuid)) {
            throw IllegalStateException("Riot account already exists")
        }

        // Call Riot API to verify PUUID
        riotAccountGateway.getAccountByPuuid(puuid.value) // throws if anything fails

        return accountRepository.insert(account) ?: throw DatabaseException("Failed to insert account")
    }

    fun isPuuidTaken(puuid: RiotPuuid): Boolean {
        return accountRepository.getAccountByPuuid(puuid.value) != null
    }

}