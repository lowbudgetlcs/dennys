package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.NewPlayer
import com.lowbudgetlcs.domain.models.PlayerId
import com.lowbudgetlcs.domain.models.PlayerWithAccounts
import com.lowbudgetlcs.domain.models.toPlayerId
import com.lowbudgetlcs.repositories.IPlayerRepository
import org.jooq.DSLContext
import org.jooq.storage.tables.references.PLAYERS

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
        TODO("Not yet implemented")
    }

    override fun getById(id: PlayerId): PlayerWithAccounts? {
        TODO("Not yet implemented")
    }
}