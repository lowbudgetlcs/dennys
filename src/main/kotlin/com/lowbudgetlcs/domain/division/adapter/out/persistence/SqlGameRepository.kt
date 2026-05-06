package com.lowbudgetlcs.domain.division.adapter.out.persistence

import com.lowbudgetlcs.domain.division.core.event.model.types.Shortcode
import com.lowbudgetlcs.domain.division.core.event.model.types.toShortcode
import com.lowbudgetlcs.domain.division.core.series.model.Game
import com.lowbudgetlcs.domain.division.core.series.model.NewGame
import com.lowbudgetlcs.domain.division.core.series.model.types.GameId
import com.lowbudgetlcs.domain.division.core.series.model.types.toGameId
import com.lowbudgetlcs.domain.division.core.series.model.types.toSeriesId
import com.lowbudgetlcs.domain.division.core.series.port.IGameRepository
import com.lowbudgetlcs.domain.team.core.model.types.toTeamId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.GAMES

class SqlGameRepository(
    private val dsl: DSLContext,
) : IGameRepository {
    override suspend fun getById(id: GameId) = withContext(Dispatchers.IO) {
        selectGames().where(GAMES.ID.eq(id.value)).fetchOne()
    }?.let(::rowToGames)

    override suspend fun insert(
        newGame: NewGame,
        shortcode: Shortcode,
    ): Game? {
        val insertedId =
            withContext(Dispatchers.IO) {
                dsl
                    .insertInto(GAMES)
                    .set(GAMES.SHORTCODE, shortcode.value)
                    .set(GAMES.SERIES_ID, newGame.seriesId.value)
                    .set(GAMES.BLUE_TEAM_ID, newGame.blueTeamId.value)
                    .set(GAMES.RED_TEAM_ID, newGame.redTeamId.value)
                    .returning(GAMES.ID)
                    .fetchOne()
            }
                ?.get(GAMES.ID)
        return insertedId?.toGameId()?.let { getById(it) }
    }

    private fun selectGames() =
        dsl
            .select(
                GAMES.ID,
                GAMES.SHORTCODE,
                GAMES.BLUE_TEAM_ID,
                GAMES.RED_TEAM_ID,
                GAMES.SERIES_ID,
                GAMES.NUMBER,
            ).from(GAMES)

    fun rowToGames(row: Record): Game? {
        val gameId = row[GAMES.ID]?.toGameId() ?: return null
        val seriesId = row[GAMES.SERIES_ID]?.toSeriesId() ?: return null
        val blueTeamId = row[GAMES.BLUE_TEAM_ID]?.toTeamId() ?: return null
        val redTeamId = row[GAMES.RED_TEAM_ID]?.toTeamId() ?: return null
        val shortcode = row[GAMES.SHORTCODE]?.toShortcode() ?: return null
        val number = row[GAMES.NUMBER] ?: return null

        return Game(
            id = gameId,
            shortcode = shortcode,
            blueTeamId = blueTeamId,
            redTeamId = redTeamId,
            seriesId = seriesId,
            number = number,
            result = null,
        )
    }
}
