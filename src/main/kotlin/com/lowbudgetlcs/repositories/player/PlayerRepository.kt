package com.lowbudgetlcs.repositories.player

import com.lowbudgetlcs.domain.models.player.*
import com.lowbudgetlcs.domain.models.riot.account.RiotAccount
import com.lowbudgetlcs.domain.models.riot.account.RiotAccountId
import com.lowbudgetlcs.domain.models.riot.account.RiotPuuid
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.PLAYERS
import org.jooq.storage.tables.references.RIOT_ACCOUNTS

class PlayerRepository(
    private val dsl: DSLContext,
) : IPlayerRepository {
    override fun insert(newPlayer: NewPlayer): PlayerWithAccounts? {
        val insertedId =
            dsl
                .insertInto(PLAYERS)
                .set(PLAYERS.NAME, newPlayer.name.value)
                .returning(PLAYERS.ID)
                .fetchOne()
                ?.get(PLAYERS.ID)

        return insertedId?.toPlayerId()?.let(::getById)
    }

    override fun getAll(): List<PlayerWithAccounts> = fetchPlayerRows().mapNotNull(::rowToPlayerWithAccounts)

    override fun getById(id: PlayerId): PlayerWithAccounts? = getPlayerRowById(id)?.let(::rowToPlayerWithAccounts)

    override fun renamePlayer(
        id: PlayerId,
        newName: PlayerName,
    ): PlayerWithAccounts? {
        val updated =
            dsl
                .update(PLAYERS)
                .set(PLAYERS.NAME, newName.value)
                .where(PLAYERS.ID.eq(id.value))
                .execute()

        return if (updated > 0) getById(id) else null
    }

    override fun insertAccountToPlayer(
        playerId: PlayerId,
        accountId: RiotAccountId,
    ): PlayerWithAccounts? {
        val updated =
            dsl
                .update(RIOT_ACCOUNTS)
                .set(RIOT_ACCOUNTS.PLAYER_ID, playerId.value)
                .where(RIOT_ACCOUNTS.ID.eq(accountId.value))
                .execute()

        return if (updated > 0) getById(playerId) else null
    }

    override fun removeAccount(
        playerId: PlayerId,
        accountId: RiotAccountId,
    ): PlayerWithAccounts? {
        val updated =
            dsl
                .update(RIOT_ACCOUNTS)
                .set(RIOT_ACCOUNTS.PLAYER_ID, null as Int?)
                .where(RIOT_ACCOUNTS.ID.eq(accountId.value))
                .and(RIOT_ACCOUNTS.PLAYER_ID.eq(playerId.value))
                .execute()

        return if (updated > 0) getById(playerId) else null
    }

    // Helper functions

    private fun fetchPlayerRows() =
        dsl
            .select(PLAYERS.ID, PLAYERS.NAME)
            .from(PLAYERS)
            .fetch()

    private fun getPlayerRowById(id: PlayerId) =
        dsl
            .select(PLAYERS.ID, PLAYERS.NAME)
            .from(PLAYERS)
            .where(PLAYERS.ID.eq(id.value))
            .fetchOne()

    private fun rowToPlayerWithAccounts(row: Record): PlayerWithAccounts? {
        val playerId = row[PLAYERS.ID]?.toPlayerId() ?: return null
        val name = row[PLAYERS.NAME]?.let { PlayerName(it) } ?: return null
        val accounts = getAccountsForPlayer(playerId)

        return PlayerWithAccounts(
            id = playerId,
            name = name,
            accounts = accounts,
        )
    }

    private fun getAccountsForPlayer(playerId: PlayerId): List<RiotAccount> =
        dsl
            .select(RIOT_ACCOUNTS.ID, RIOT_ACCOUNTS.RIOT_PUUID)
            .from(RIOT_ACCOUNTS)
            .where(RIOT_ACCOUNTS.PLAYER_ID.eq(playerId.value))
            .fetch()
            .map {
                RiotAccount(
                    id = RiotAccountId(it[RIOT_ACCOUNTS.ID]!!),
                    riotPuuid = RiotPuuid(it[RIOT_ACCOUNTS.RIOT_PUUID]!!),
                    playerId = playerId,
                )
            }
}
