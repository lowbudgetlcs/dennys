package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.Game
import com.lowbudgetlcs.domain.models.GameId
import com.lowbudgetlcs.domain.models.SeriesId
import com.lowbudgetlcs.domain.models.TeamId
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
                    id = GameId(row[GAMES.ID]!!),
                    shortCode = row[GAMES.SHORTCODE]!!,
                    blueSideId = TeamId(row[GAMES.BLUE_TEAM_ID]!!),
                    redSideId = TeamId(row[GAMES.RED_TEAM_ID]!!),
                    seriesId = SeriesId(row[GAMES.SERIES_ID]!!),
                    winnerId = row[GAME_RESULTS.WINNER_TEAM_ID]?.let { TeamId(it) },
                    loserId = row[GAME_RESULTS.LOSER_TEAM_ID]?.let { TeamId(it) }
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
                id = GameId(row[GAMES.ID]!!),
                shortCode = row[GAMES.SHORTCODE]!!,
                blueSideId = TeamId(row[GAMES.BLUE_TEAM_ID]!!),
                redSideId = TeamId(row[GAMES.RED_TEAM_ID]!!),
                seriesId = SeriesId(row[GAMES.SERIES_ID]!!),
                winnerId = row[GAME_RESULTS.WINNER_TEAM_ID]?.let { TeamId(it) },
                loserId = row[GAME_RESULTS.LOSER_TEAM_ID]?.let { TeamId(it) }
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
                    id = GameId(row[GAMES.ID]!!),
                    shortCode = row[GAMES.SHORTCODE]!!,
                    blueSideId = TeamId(row[GAMES.BLUE_TEAM_ID]!!),
                    redSideId = TeamId(row[GAMES.RED_TEAM_ID]!!),
                    seriesId = SeriesId(row[GAMES.SERIES_ID]!!),
                    winnerId = row[GAME_RESULTS.WINNER_TEAM_ID]?.let { TeamId(it) },
                    loserId = row[GAME_RESULTS.LOSER_TEAM_ID]?.let { TeamId(it) }
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
                    id = GameId(row[GAMES.ID]!!),
                    shortCode = row[GAMES.SHORTCODE]!!,
                    blueSideId = TeamId(row[GAMES.BLUE_TEAM_ID]!!),
                    redSideId = TeamId(row[GAMES.RED_TEAM_ID]!!),
                    seriesId = SeriesId(row[GAMES.SERIES_ID]!!),
                    winnerId = row[GAME_RESULTS.WINNER_TEAM_ID]?.let { TeamId(it) },
                    loserId = row[GAME_RESULTS.LOSER_TEAM_ID]?.let { TeamId(it) }
                )
            }
}