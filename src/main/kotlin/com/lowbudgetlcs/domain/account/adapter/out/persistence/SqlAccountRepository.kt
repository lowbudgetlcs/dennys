package com.lowbudgetlcs.domain.account.adapter.out.persistence

import com.lowbudgetlcs.domain.account.core.model.Account
import com.lowbudgetlcs.domain.account.core.model.NewAccount
import com.lowbudgetlcs.domain.account.core.model.types.AccountId
import com.lowbudgetlcs.domain.account.core.model.types.Puuid
import com.lowbudgetlcs.domain.account.core.model.types.toAccountId
import com.lowbudgetlcs.domain.account.core.port.IAccountRepository
import com.lowbudgetlcs.domain.player.core.model.types.PlayerId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.RIOT_ACCOUNTS

class SqlAccountRepository(
    private val dsl: DSLContext,
) : IAccountRepository {
    override suspend fun getAll(): List<Account> = withContext(Dispatchers.IO) {
        selectAccounts().fetch()
    }.mapNotNull(::rowToAccount)

    override suspend fun getById(accountId: AccountId): Account? =
        withContext(Dispatchers.IO) {
            selectAccounts().where(RIOT_ACCOUNTS.ID.eq(accountId.value)).fetchOne()
        }?.let(::rowToAccount)

    override suspend fun getAccountByPuuid(puuid: Puuid): Account? =
        withContext(Dispatchers.IO) {
            selectAccounts().where(RIOT_ACCOUNTS.RIOT_PUUID.eq(puuid.value)).fetchOne()
        }?.let(::rowToAccount)

    override suspend fun insert(newAccount: NewAccount): Account? {
        val insertedId =
            withContext(Dispatchers.IO) {
                dsl
                    .insertInto(RIOT_ACCOUNTS)
                    .set(RIOT_ACCOUNTS.RIOT_PUUID, newAccount.puuid.value)
                    .set(RIOT_ACCOUNTS.PLAYER_ID, newAccount.playerId?.value)
                    .returning(RIOT_ACCOUNTS.ID)
                    .fetchOne()
            }
                ?.get(RIOT_ACCOUNTS.ID)

        return insertedId?.toAccountId()?.let { getById(it) }
    }

    override suspend fun updatePlayerId(
        accountId: AccountId,
        playerId: PlayerId?,
    ): Account? {
        val updatedId =
            withContext(Dispatchers.IO) {
                dsl
                    .update(RIOT_ACCOUNTS)
                    .set(RIOT_ACCOUNTS.PLAYER_ID, playerId?.value)
                    .where(RIOT_ACCOUNTS.ID.eq(accountId.value))
                    .returning(RIOT_ACCOUNTS.ID)
                    .fetchOne()
            }
                ?.get(RIOT_ACCOUNTS.ID)
        return updatedId?.toAccountId()?.let { getById(it) }
    }

    private fun selectAccounts() =
        dsl.select(RIOT_ACCOUNTS.ID, RIOT_ACCOUNTS.RIOT_PUUID, RIOT_ACCOUNTS.PLAYER_ID).from(RIOT_ACCOUNTS)

    private fun rowToAccount(row: Record): Account? {
        val accountId = row[RIOT_ACCOUNTS.ID]?.toAccountId() ?: return null
        val puuid = row[RIOT_ACCOUNTS.RIOT_PUUID]?.let { Puuid(it) } ?: return null
        val playerId = row[RIOT_ACCOUNTS.PLAYER_ID]?.let { PlayerId(it) }

        return Account(
            id = accountId,
            puuid = puuid,
            playerId = playerId,
        )
    }
}
