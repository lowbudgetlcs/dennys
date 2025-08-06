package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.NewRiotAccount
import com.lowbudgetlcs.domain.models.RiotAccount
import com.lowbudgetlcs.domain.models.RiotAccountId

interface IAccountRepository {
    fun insert(newAccount: NewRiotAccount): RiotAccount?
    fun getAll(): List<RiotAccount>
    fun getById(accountId: RiotAccountId): RiotAccount?
    fun getAccountByPuuid(puuid: String): RiotAccount?
}