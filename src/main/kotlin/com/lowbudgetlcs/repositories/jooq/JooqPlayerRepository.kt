package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.repositories.IPlayerRepository
import org.jooq.DSLContext
import org.jooq.storage.tables.references.PLAYERS
import org.jooq.storage.tables.references.RIOT_ACCOUNTS

class JooqPlayerRepository(
    private val dsl: DSLContext
) : IPlayerRepository {

    override fun insert(newPlayer: NewPlayer): PlayerWithAccounts? {
        return dsl.insertInto(PLAYERS)
            .set(PLAYERS.NAME, newPlayer.name.name)
            .returning(PLAYERS.ID)
            .fetchOne()
            ?.let { row ->
                PlayerWithAccounts(
                    id = row[PLAYERS.ID]!!.toPlayerId(),
                    name = newPlayer.name,
                    accounts = emptyList()
                )
            }
    }

    override fun getAll(): List<PlayerWithAccounts> {
        val players = dsl
            .select(PLAYERS.ID, PLAYERS.NAME)
            .from(PLAYERS)
            .fetch()

        return players.map { row ->
            val playerId = row[PLAYERS.ID]!!.toPlayerId()
            val name = PlayerName(row[PLAYERS.NAME]!!)
            val accounts = getAccountsForPlayer(playerId)

            PlayerWithAccounts(
                id = playerId,
                name = name,
                accounts = accounts
            )
        }
    }

    override fun getById(id: PlayerId): PlayerWithAccounts? {
        val row = dsl
            .select(PLAYERS.ID, PLAYERS.NAME)
            .from(PLAYERS)
            .where(PLAYERS.ID.eq(id.value))
            .fetchOne() ?: return null

        val playerId = row[PLAYERS.ID]!!.toPlayerId()
        val name = PlayerName(row[PLAYERS.NAME]!!)
        val accounts = getAccountsForPlayer(playerId)

        return PlayerWithAccounts(
            id = playerId,
            name = name,
            accounts = accounts
        )
    }

    override fun renamePlayer(id: PlayerId, newName: PlayerName): PlayerWithAccounts? {
        val updatedRows = dsl.update(PLAYERS)
            .set(PLAYERS.NAME, newName.name)
            .where(PLAYERS.ID.eq(id.value))
            .execute()

        return if (updatedRows > 0) getById(id) else null
    }

    private fun getAccountsForPlayer(playerId: PlayerId): List<RiotAccount> {
        return dsl.select(RIOT_ACCOUNTS.ID, RIOT_ACCOUNTS.RIOT_PUUID)
            .from(RIOT_ACCOUNTS)
            .where(RIOT_ACCOUNTS.PLAYER_ID.eq(playerId.value))
            .fetch()
            .map {
                RiotAccount(
                    id = RiotAccountId(it[RIOT_ACCOUNTS.ID]!!),
                    riotPuuid = RiotPuuid(it[RIOT_ACCOUNTS.RIOT_PUUID]!!),
                    playerId = playerId
                )
            }
    }

    override fun createAccountRecord(riotPuuid: RiotPuuid): RiotAccount {
        val inserted = dsl.insertInto(RIOT_ACCOUNTS)
            .set(RIOT_ACCOUNTS.RIOT_PUUID, riotPuuid.value)
            .returning(RIOT_ACCOUNTS.ID)
            .fetchOne() ?: throw IllegalStateException("Failed to insert Riot account")

        return RiotAccount(
            id = RiotAccountId(inserted[RIOT_ACCOUNTS.ID]!!),
            riotPuuid = riotPuuid,
            playerId = null
        )
    }

    override fun insertAccountToPlayer(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts? {
        val updated = dsl.update(RIOT_ACCOUNTS)
            .set(RIOT_ACCOUNTS.PLAYER_ID, playerId.value)
            .where(RIOT_ACCOUNTS.ID.eq(accountId.value))
            .execute()

        return if (updated > 0) getById(playerId) else null
    }

    override fun removeAccount(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts? {
        dsl.deleteFrom(RIOT_ACCOUNTS)
            .where(RIOT_ACCOUNTS.ID.eq(accountId.value))
            .and(RIOT_ACCOUNTS.PLAYER_ID.eq(playerId.value))
            .execute()

        return getById(playerId)
    }
}