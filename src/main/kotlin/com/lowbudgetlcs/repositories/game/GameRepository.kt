package com.lowbudgetlcs.repositories.game

import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.domain.models.riot.tournament.Shortcode
import com.lowbudgetlcs.domain.models.riot.tournament.toShortcode
import com.lowbudgetlcs.domain.models.team.toTeamId
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.GAMES

class GameRepository(private val dsl: DSLContext) : IGameRepository {
    override fun getById(id: GameId) = selectGames().where(GAMES.ID.eq(id.value)).fetchOne()?.let(::rowToGames)
    override fun insert(
        newGame: NewGame, shortcode: Shortcode, seriesId: SeriesId
    ): Game? {
        val insertedId =
            dsl.insertInto(GAMES).set(GAMES.SHORTCODE, shortcode.value).set(GAMES.SERIES_ID, seriesId.value)
                .set(GAMES.BLUE_TEAM_ID, newGame.blueTeamId.value).set(GAMES.RED_TEAM_ID, newGame.redTeamId.value)
                .returning(GAMES.ID).fetchOne()?.get(GAMES.ID)
        return insertedId?.toGameId()?.let(::getById)
    }

    private fun selectGames() = dsl.select(
        GAMES.ID, GAMES.SHORTCODE, GAMES.BLUE_TEAM_ID, GAMES.RED_TEAM_ID, GAMES.SERIES_ID, GAMES.NUMBER
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
            result = null
        )
    }
}