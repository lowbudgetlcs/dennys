package com.lowbudgetlcs.domain.account.core.port

import com.lowbudgetlcs.domain.account.core.model.Account
import com.lowbudgetlcs.domain.account.core.model.NewAccount
import com.lowbudgetlcs.domain.account.core.model.types.AccountId
import com.lowbudgetlcs.domain.account.core.model.types.Puuid
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
