package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.Player
import com.lowbudgetlcs.domain.models.toPlayerId
import com.lowbudgetlcs.domain.models.toPlayerName
import org.jooq.DSLContext
import org.jooq.storage.tables.references.PLAYERS

class JooqPlayerRepository(private val dsl: DSLContext) {

    fun getAll(): List<Player> =
        dsl
            .select(
                PLAYERS.ID, PLAYERS.NAME
            ).from(PLAYERS).fetch().map { row ->
                Player(
                    id = row[PLAYERS.ID]!!.toPlayerId(),
                    name = row[PLAYERS.NAME]!!.toPlayerName()
                )
            }

}