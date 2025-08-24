package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.NewRiotAccount
import com.lowbudgetlcs.domain.models.RiotAccount
import com.lowbudgetlcs.domain.models.RiotAccountId
import com.lowbudgetlcs.domain.models.RiotPuuid
import com.lowbudgetlcs.gateways.IRiotAccountGateway
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.IAccountRepository

class AccountService(
    private val accountRepository: IAccountRepository,
    private val riotAccountGateway: IRiotAccountGateway
) {

    suspend fun createAccount(account: NewRiotAccount): RiotAccount {
        val puuid = account.riotPuuid

        if (isPuuidTaken(puuid)) {
            throw IllegalStateException("Riot account already exists")
        }

        // Call Riot API to verify PUUID
        riotAccountGateway.getAccountByPuuid(puuid.value) // throws if anything fails

        return accountRepository.insert(account) ?: throw DatabaseException("Failed to insert account")
    }

    fun getAccount(accountId: RiotAccountId): RiotAccount {
        return accountRepository.getById(accountId) ?: throw NoSuchElementException("Account not found")
    }

    fun getAllAccounts(): List<RiotAccount> = accountRepository.getAll()

    fun isPuuidTaken(puuid: RiotPuuid): Boolean {
        return accountRepository.getAccountByPuuid(puuid.value) != null
    }

}