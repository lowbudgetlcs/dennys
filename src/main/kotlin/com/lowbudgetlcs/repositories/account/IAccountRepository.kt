package com.lowbudgetlcs.repositories.account

import com.lowbudgetlcs.domain.account.models.Account
import com.lowbudgetlcs.domain.account.models.NewAccount
import com.lowbudgetlcs.domain.account.models.types.AccountId
import com.lowbudgetlcs.domain.account.models.types.Puuid
import com.lowbudgetlcs.domain.player.models.types.PlayerId

interface IAccountRepository {
    fun getAll(): List<Account>

    fun getById(accountId: AccountId): Account?

    fun getAccountByPuuid(puuid: Puuid): Account?

    fun insert(newAccount: NewAccount): Account?

    fun updatePlayerId(
        accountId: AccountId,
        playerId: PlayerId?,
    ): Account?
}
