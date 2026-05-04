package com.lowbudgetlcs.domain.account.core.port

import com.lowbudgetlcs.domain.account.core.model.Account
import com.lowbudgetlcs.domain.account.core.model.NewAccount
import com.lowbudgetlcs.domain.account.core.model.types.AccountId

interface IAccountService {
    suspend fun getAccount(accountId: AccountId): Account
    suspend fun getAllAccounts(): List<Account>
    suspend fun createAccount(account: NewAccount): Account
}
