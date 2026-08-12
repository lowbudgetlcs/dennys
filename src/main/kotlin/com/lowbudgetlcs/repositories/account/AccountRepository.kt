package com.lowbudgetlcs.repositories.account

import com.lowbudgetlcs.domain.account.models.Account
import com.lowbudgetlcs.domain.account.models.NewAccount
import com.lowbudgetlcs.domain.account.models.toAccountId
import com.lowbudgetlcs.domain.account.models.types.AccountId
import com.lowbudgetlcs.domain.account.models.types.Puuid
import com.lowbudgetlcs.domain.player.models.types.PlayerId
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.RIOT_ACCOUNTS

class AccountRepository(
    private val dsl: DSLContext,
) : IAccountRepository {
    override fun getAll(): List<Account> = selectAccounts().fetch().mapNotNull(::rowToAccount)

    override fun getById(accountId: AccountId): Account? =
        selectAccounts().where(RIOT_ACCOUNTS.ID.eq(accountId.value)).fetchOne()?.let(::rowToAccount)

    override fun getAccountByPuuid(puuid: Puuid): Account? =
        selectAccounts().where(RIOT_ACCOUNTS.RIOT_PUUID.eq(puuid.value)).fetchOne()?.let(::rowToAccount)

    override fun getByPlayerId(playerId: PlayerId): List<Account> =
        selectAccounts()
            .where(RIOT_ACCOUNTS.PLAYER_ID.eq(playerId.value))
            .fetch()
            .mapNotNull(::rowToAccount)

    override fun insert(newAccount: NewAccount): Account? {
        val insertedId =
            dsl
                .insertInto(RIOT_ACCOUNTS)
                .set(RIOT_ACCOUNTS.RIOT_PUUID, newAccount.puuid.value)
                .set(RIOT_ACCOUNTS.PLAYER_ID, newAccount.playerId?.value)
                .returning(RIOT_ACCOUNTS.ID)
                .fetchOne()
                ?.get(RIOT_ACCOUNTS.ID)

        return insertedId?.toAccountId()?.let(::getById)
    }

    override fun updatePlayerId(
        accountId: AccountId,
        playerId: PlayerId?,
    ): Account? {
        val updatedId =
            dsl
                .update(RIOT_ACCOUNTS)
                .set(RIOT_ACCOUNTS.PLAYER_ID, playerId?.value)
                .where(RIOT_ACCOUNTS.ID.eq(accountId.value))
                .returning(RIOT_ACCOUNTS.ID)
                .fetchOne()
                ?.get(RIOT_ACCOUNTS.ID)
        return updatedId?.toAccountId()?.let(::getById)
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
