package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.repositories.IAccountRepository
import org.jooq.DSLContext
import org.jooq.storage.tables.references.RIOT_ACCOUNTS

class JooqAccountRepository(
    private val dsl: DSLContext
) : IAccountRepository {

    override fun insert(newAccount: NewRiotAccount): RiotAccount? {
        return dsl.insertInto(RIOT_ACCOUNTS)
            .set(RIOT_ACCOUNTS.RIOT_PUUID, newAccount.riotPuuid.value)
            .returning(RIOT_ACCOUNTS.ID)
            .fetchOne()
            ?.let { record ->
                RiotAccount(
                    id = record[RIOT_ACCOUNTS.ID]!!.toRiotAccountId(),
                    riotPuuid = newAccount.riotPuuid,
                    playerId = null
                )
            }
    }

    override fun getAll(): List<RiotAccount> {
        val accounts = dsl
            .select(RIOT_ACCOUNTS.ID, RIOT_ACCOUNTS.RIOT_PUUID, RIOT_ACCOUNTS.PLAYER_ID)
            .from(RIOT_ACCOUNTS)
            .fetch()

        return accounts.map { row ->
            val accountId = row[RIOT_ACCOUNTS.ID]!!.toRiotAccountId()
            val riotPuuid = RiotPuuid(row[RIOT_ACCOUNTS.RIOT_PUUID]!!)
            val playerId = row[RIOT_ACCOUNTS.PLAYER_ID]?.let { PlayerId(it) }

            RiotAccount(
                id = accountId,
                riotPuuid = riotPuuid,
                playerId = playerId
            )
        }
    }

    override fun getById(accountId: RiotAccountId): RiotAccount? {
        return dsl
            .select(RIOT_ACCOUNTS.ID, RIOT_ACCOUNTS.RIOT_PUUID, RIOT_ACCOUNTS.PLAYER_ID)
            .from(RIOT_ACCOUNTS)
            .where(RIOT_ACCOUNTS.ID.eq(accountId.value))
            .fetchOne()
            ?.let { row ->
                RiotAccount(
                    id = row[RIOT_ACCOUNTS.ID]!!.toRiotAccountId(),
                    riotPuuid = RiotPuuid(row[RIOT_ACCOUNTS.RIOT_PUUID]!!),
                    playerId = row[RIOT_ACCOUNTS.PLAYER_ID]?.let { PlayerId(it) }
                )
            }
    }

}