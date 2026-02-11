package com.lowbudgetlcs.domain.services.account

import com.lowbudgetlcs.domain.models.player.account.Account
import com.lowbudgetlcs.domain.models.player.account.AccountId
import com.lowbudgetlcs.domain.models.player.account.NewAccount

interface IAccountService {
    fun getAccount(accountId: AccountId): Account

    fun getAllAccounts(): List<Account>

    suspend fun createAccount(account: NewAccount): Account
}
