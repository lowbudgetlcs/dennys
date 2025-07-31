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
        TODO("Not yet implemented")
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
}