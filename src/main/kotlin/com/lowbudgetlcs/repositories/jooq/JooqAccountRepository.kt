package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.repositories.IAccountRepository
import org.jooq.DSLContext
import org.jooq.storage.tables.references.RIOT_ACCOUNTS
import org.jooq.Record

class JooqAccountRepository(
    private val dsl: DSLContext
) : IAccountRepository {

    override fun insert(newAccount: NewRiotAccount): RiotAccount? {
        val insertedId = dsl.insertInto(RIOT_ACCOUNTS)
            .set(RIOT_ACCOUNTS.RIOT_PUUID, newAccount.riotPuuid.value)
            .returning(RIOT_ACCOUNTS.ID)
            .fetchOne()
            ?.get(RIOT_ACCOUNTS.ID)

        return insertedId?.toRiotAccountId()?.let(::getById)
    }

    override fun getAll(): List<RiotAccount> {
        return fetchAccountRows().mapNotNull(::rowToRiotAccount)
    }

    override fun getById(accountId: RiotAccountId): RiotAccount? {
        return getAccountRowById(accountId)?.let(::rowToRiotAccount)
    }

    override fun getAccountByPuuid(puuid: String): RiotAccount? =
        getAccountRowByPuuid(puuid)?.let(::rowToRiotAccount)

    // Helpers

    private fun fetchAccountRows() = dsl
        .select(RIOT_ACCOUNTS.ID, RIOT_ACCOUNTS.RIOT_PUUID, RIOT_ACCOUNTS.PLAYER_ID)
        .from(RIOT_ACCOUNTS)
        .fetch()

    private fun getAccountRowById(accountId: RiotAccountId) = dsl
        .select(RIOT_ACCOUNTS.ID, RIOT_ACCOUNTS.RIOT_PUUID, RIOT_ACCOUNTS.PLAYER_ID)
        .from(RIOT_ACCOUNTS)
        .where(RIOT_ACCOUNTS.ID.eq(accountId.value))
        .fetchOne()

    private fun getAccountRowByPuuid(puuid: String) = dsl
        .select(RIOT_ACCOUNTS.ID, RIOT_ACCOUNTS.RIOT_PUUID, RIOT_ACCOUNTS.PLAYER_ID)
        .from(RIOT_ACCOUNTS)
        .where(RIOT_ACCOUNTS.RIOT_PUUID.eq(puuid))
        .fetchOne()

    private fun rowToRiotAccount(row: Record): RiotAccount? {
        val accountId = row[RIOT_ACCOUNTS.ID]?.toRiotAccountId() ?: return null
        val riotPuuid = row[RIOT_ACCOUNTS.RIOT_PUUID]?.let { RiotPuuid(it) } ?: return null
        val playerId = row[RIOT_ACCOUNTS.PLAYER_ID]?.let { PlayerId(it) }

        return RiotAccount(
            id = accountId,
            riotPuuid = riotPuuid,
            playerId = playerId
        )
    }
}