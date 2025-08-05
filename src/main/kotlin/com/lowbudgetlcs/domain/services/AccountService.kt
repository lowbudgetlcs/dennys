package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.NewRiotAccount
import com.lowbudgetlcs.domain.models.RiotAccount
import com.lowbudgetlcs.domain.models.RiotAccountId
import com.lowbudgetlcs.domain.models.RiotPuuid
import com.lowbudgetlcs.gateways.IRiotAccountGateway
import com.lowbudgetlcs.repositories.IAccountRepository

class AccountService(
    private val accountRepository: IAccountRepository,
    private val riotAccountGateway: IRiotAccountGateway
) {

    suspend fun createAccount(account: NewRiotAccount): RiotAccount? {
        val puuid = account.riotPuuid

        if (isPuuidTaken(puuid)) {
            throw IllegalStateException("Riot account already exists")
        }

        // Call Riot API to verify PUUID
        riotAccountGateway.getAccountByPuuid(puuid.value)
            ?: throw NoSuchElementException("Riot account not found from Riot API")

        return accountRepository.insert(account)
    }

    fun getAccount(accountId: RiotAccountId): RiotAccount? = accountRepository.getById(accountId)

    fun getAllAccounts(): List<RiotAccount> = accountRepository.getAll()

    fun isPuuidTaken(puuid: RiotPuuid): Boolean {
        return accountRepository.getAll().any { it.riotPuuid.value == puuid.value }
    }

}