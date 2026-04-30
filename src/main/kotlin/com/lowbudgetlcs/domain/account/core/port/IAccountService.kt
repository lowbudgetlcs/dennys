package com.lowbudgetlcs.domain.account.core.port

import com.lowbudgetlcs.domain.account.core.model.Account
import com.lowbudgetlcs.domain.account.core.model.NewAccount
import com.lowbudgetlcs.domain.account.core.model.types.AccountId

interface IAccountService {
    fun getAccount(accountId: AccountId): Account

    fun getAllAccounts(): List<Account>

    suspend fun createAccount(account: NewAccount): Account
}
