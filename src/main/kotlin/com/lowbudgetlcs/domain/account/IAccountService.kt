package com.lowbudgetlcs.domain.account

import com.lowbudgetlcs.domain.account.models.Account
import com.lowbudgetlcs.domain.account.models.NewAccount
import com.lowbudgetlcs.domain.account.models.types.AccountId

interface IAccountService {
    fun getAccount(accountId: AccountId): Account

    fun getAllAccounts(): List<Account>

    suspend fun createAccount(account: NewAccount): Account
}
