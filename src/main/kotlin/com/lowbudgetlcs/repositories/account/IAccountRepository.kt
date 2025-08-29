package com.lowbudgetlcs.repositories.account

import com.lowbudgetlcs.domain.models.riot.account.NewRiotAccount
import com.lowbudgetlcs.domain.models.riot.account.RiotAccount
import com.lowbudgetlcs.domain.models.riot.account.RiotAccountId

interface IAccountRepository {
    fun insert(newAccount: NewRiotAccount): RiotAccount?
    fun getAll(): List<RiotAccount>
    fun getById(accountId: RiotAccountId): RiotAccount?
    fun getAccountByPuuid(puuid: String): RiotAccount?
}