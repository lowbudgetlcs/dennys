package com.lowbudgetlcs.domain.services.account

import com.lowbudgetlcs.domain.models.riot.NewRiotAccount
import com.lowbudgetlcs.domain.models.riot.RiotAccount
import com.lowbudgetlcs.domain.models.riot.RiotAccountId

interface IAccountService {
    fun getAccount(accountId: RiotAccountId): RiotAccount
    fun getAllAccounts(): List<RiotAccount>
    suspend fun createAccount(account: NewRiotAccount): RiotAccount
}