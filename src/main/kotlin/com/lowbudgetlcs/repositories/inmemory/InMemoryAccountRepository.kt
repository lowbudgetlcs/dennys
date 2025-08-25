package com.lowbudgetlcs.repositories.inmemory

import com.lowbudgetlcs.domain.models.riot.NewRiotAccount
import com.lowbudgetlcs.domain.models.riot.RiotAccount
import com.lowbudgetlcs.domain.models.riot.RiotAccountId
import com.lowbudgetlcs.repositories.IAccountRepository

class InMemoryAccountRepository : IAccountRepository {
    private val accounts = mutableListOf<RiotAccount>()
    private var nextId = 0

    override fun insert(newAccount: NewRiotAccount): RiotAccount {
        val account = RiotAccount(
            id = RiotAccountId(nextId++),
            riotPuuid = newAccount.riotPuuid,
            playerId = null
        )
        accounts.add(account)
        return account
    }

    override fun getAll(): List<RiotAccount> = accounts.toList()

    override fun getById(accountId: RiotAccountId): RiotAccount? {
        return accounts.find { it.id == accountId }
    }

    override fun getAccountByPuuid(puuid: String): RiotAccount? {
        return accounts.find { it.riotPuuid.value == puuid }
    }

    fun clear() {
        accounts.clear()
        nextId = 0
    }
}