package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.player.Player
import org.jooq.DSLContext
import org.jooq.storage.tables.references.PLAYERS

class JooqPlayerRepository(private val dsl: DSLContext) {

    fun getAll(): List<Player> =
        dsl
            .select(
                PLAYERS.ID, PLAYERS.NAME, PLAYERS.EVENT_ID, PLAYERS.TEAM_ID
            ).from(PLAYERS).fetch().map { row ->
                Player(
                    id = row[PLAYERS.ID]!!,
                    name = row[PLAYERS.NAME]!!,
                    teamId = row[PLAYERS.TEAM_ID],
                    eventId = row[PLAYERS.EVENT_ID]
                )
            }

}