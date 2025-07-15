package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.Player
import com.lowbudgetlcs.domain.models.PlayerId
import com.lowbudgetlcs.domain.models.PlayerName
import org.jooq.DSLContext
import org.jooq.storage.tables.references.PLAYERS

class JooqPlayerRepository(private val dsl: DSLContext) {

    fun getAll(): List<Player> =
        dsl
            .select(
                PLAYERS.ID, PLAYERS.NAME
            ).from(PLAYERS).fetch().map { row ->
                Player(
                    id = PlayerId(row[PLAYERS.ID]!!),
                    name = PlayerName(row[PLAYERS.NAME]!!)
                )
            }

}