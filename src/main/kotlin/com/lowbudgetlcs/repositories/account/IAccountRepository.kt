package com.lowbudgetlcs.repositories.account

import com.lowbudgetlcs.domain.models.player.PlayerId
import com.lowbudgetlcs.domain.models.player.account.Account
import com.lowbudgetlcs.domain.models.player.account.AccountId
import com.lowbudgetlcs.domain.models.player.account.NewAccount
import com.lowbudgetlcs.domain.models.player.account.Puuid

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
