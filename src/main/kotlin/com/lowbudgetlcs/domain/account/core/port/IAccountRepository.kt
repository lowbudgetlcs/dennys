package com.lowbudgetlcs.domain.account.core.port

import com.lowbudgetlcs.domain.account.core.model.Account
import com.lowbudgetlcs.domain.account.core.model.NewAccount
import com.lowbudgetlcs.domain.account.core.model.types.AccountId
import com.lowbudgetlcs.domain.account.core.model.types.Puuid
import com.lowbudgetlcs.domain.player.core.model.types.PlayerId

interface IAccountRepository {
    suspend fun getAll(): List<Account>
    suspend fun getById(accountId: AccountId): Account?
    suspend fun getAccountByPuuid(puuid: Puuid): Account?
    suspend fun insert(newAccount: NewAccount): Account?
    suspend fun updatePlayerId(
        accountId: AccountId,
        playerId: PlayerId?,
    ): Account?
}
