package com.lowbudgetlcs.domain.account.services

import com.lowbudgetlcs.domain.account.models.Account
import com.lowbudgetlcs.domain.account.models.NewAccount
import com.lowbudgetlcs.domain.account.models.AccountId

interface IAccountService {
    fun getAccount(accountId: AccountId): Account

    fun getAllAccounts(): List<Account>

    suspend fun createAccount(account: NewAccount): Account
}
