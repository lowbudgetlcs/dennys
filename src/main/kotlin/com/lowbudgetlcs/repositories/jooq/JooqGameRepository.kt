package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.game.Game
import org.jooq.DSLContext
import org.jooq.storage.tables.references.GAMES
import org.jooq.storage.tables.references.GAME_RESULTS

class JooqGameRepository(private val dsl: DSLContext) {

    fun getAll(): List<Game> =
        dsl
            .select(
                GAMES.ID, GAMES.SHORTCODE, GAMES.BLUE_TEAM_ID, GAMES.RED_TEAM_ID, GAMES.SERIES_ID,
                GAME_RESULTS.WINNER_TEAM_ID, GAME_RESULTS.LOSER_TEAM_ID
            )
            .from(GAMES)
            .leftJoin(GAME_RESULTS)
            .on(GAME_RESULTS.GAME_ID.eq(GAMES.ID))
            .fetch()
            .map { row ->
                Game(
                    id = row[GAMES.ID]!!,
                    shortCode = row[GAMES.SHORTCODE]!!,
                    blueSideId = row[GAMES.BLUE_TEAM_ID]!!,
                    redSideId = row[GAMES.RED_TEAM_ID]!!,
                    seriesId = row[GAMES.SERIES_ID]!!,
                    winnerId = row[GAME_RESULTS.WINNER_TEAM_ID],
                    loserId = row[GAME_RESULTS.LOSER_TEAM_ID]
                )
            }

    fun findById(id: Int): Game? = dsl
        .select(
            GAMES.ID, GAMES.SHORTCODE, GAMES.BLUE_TEAM_ID, GAMES.RED_TEAM_ID, GAMES.SERIES_ID,
            GAME_RESULTS.WINNER_TEAM_ID, GAME_RESULTS.LOSER_TEAM_ID
        )
        .from(GAMES)
        .leftJoin(GAME_RESULTS)
        .on(GAMES.ID.eq(GAME_RESULTS.GAME_ID))
        .where(GAMES.ID.eq(id))
        .fetchOne()
        ?.let { row ->

            Game(
                id = row[GAMES.ID]!!,
                shortCode = row[GAMES.SHORTCODE]!!,
                blueSideId = row[GAMES.BLUE_TEAM_ID]!!,
                redSideId = row[GAMES.RED_TEAM_ID]!!,
                seriesId = row[GAMES.SERIES_ID]!!,
                winnerId = row[GAME_RESULTS.WINNER_TEAM_ID],
                loserId = row[GAME_RESULTS.LOSER_TEAM_ID]
            )
        }

    fun findByShortcode(shortcode: String): Game? =
        dsl
            .select(
                GAMES.ID, GAMES.SHORTCODE, GAMES.BLUE_TEAM_ID, GAMES.RED_TEAM_ID, GAMES.SERIES_ID,
                GAME_RESULTS.WINNER_TEAM_ID, GAME_RESULTS.LOSER_TEAM_ID
            )
            .from(GAMES)
            .leftJoin(GAME_RESULTS)
            .on(GAMES.ID.eq(GAME_RESULTS.GAME_ID))
            .where(GAMES.SHORTCODE.eq(shortcode))
            .fetchOne()
            ?.let { row ->
                Game(
                    id = row[GAMES.ID]!!,
                    shortCode = row[GAMES.SHORTCODE]!!,
                    blueSideId = row[GAMES.BLUE_TEAM_ID]!!,
                    redSideId = row[GAMES.RED_TEAM_ID]!!,
                    seriesId = row[GAMES.SERIES_ID]!!,
                    winnerId = row[GAME_RESULTS.WINNER_TEAM_ID],
                    loserId = row[GAME_RESULTS.LOSER_TEAM_ID]
                )
            }

    fun findBySeriesIdAndTeamId(
        teamId: Int,
        seriesId: Int
    ): List<Game> =
        dsl
            .select(
                GAMES.ID, GAMES.SHORTCODE, GAMES.BLUE_TEAM_ID, GAMES.RED_TEAM_ID, GAMES.SERIES_ID,
                GAME_RESULTS.WINNER_TEAM_ID, GAME_RESULTS.LOSER_TEAM_ID
            )
            .from(GAMES)
            .leftJoin(GAME_RESULTS)
            .on(GAMES.ID.eq(GAME_RESULTS.GAME_ID))
            .where(GAMES.BLUE_TEAM_ID.eq(teamId).or(GAMES.RED_TEAM_ID.eq(teamId)))
            .and(GAMES.SERIES_ID.eq(seriesId))
            .fetch().map { row ->
                Game(
                    id = row[GAMES.ID]!!,
                    shortCode = row[GAMES.SHORTCODE]!!,
                    blueSideId = row[GAMES.BLUE_TEAM_ID]!!,
                    redSideId = row[GAMES.RED_TEAM_ID]!!,
                    seriesId = row[GAMES.SERIES_ID]!!,
                    winnerId = row[GAME_RESULTS.WINNER_TEAM_ID],
                    loserId = row[GAME_RESULTS.LOSER_TEAM_ID]
                )
            }
}