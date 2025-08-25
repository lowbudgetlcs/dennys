package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.riot.NewRiotAccount
import com.lowbudgetlcs.domain.models.riot.RiotAccount
import com.lowbudgetlcs.domain.models.riot.RiotAccountId

interface IAccountRepository {
    fun insert(newAccount: NewRiotAccount): RiotAccount?
    fun getAll(): List<RiotAccount>
    fun getById(accountId: RiotAccountId): RiotAccount?
    fun getAccountByPuuid(puuid: String): RiotAccount?
}