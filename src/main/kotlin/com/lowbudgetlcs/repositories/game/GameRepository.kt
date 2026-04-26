package com.lowbudgetlcs.repositories.game

import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.event.models.toShortcode
import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.NewGame
import com.lowbudgetlcs.domain.series.game.models.toGameId
import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.series.models.toSeriesId
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.toTeamId
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.GAMES
import org.jooq.storage.tables.references.GAME_RESULTS

class GameRepository(
    private val dsl: DSLContext,
) : IGameRepository {
    override fun getById(id: GameId) = selectGames().where(GAMES.ID.eq(id.value)).fetchOne()?.let(::rowToGame)

    override fun getBySeriesId(id: SeriesId): List<Game> =
        selectGames().where(GAMES.SERIES_ID.eq(id.value)).fetch().map(::rowToGame)

    override fun getByShortcode(shortcode: Shortcode): Game? =
        selectGames().where(GAMES.SHORTCODE.eq(shortcode.value)).fetchOne()?.let(::rowToGame)

    override fun insert(
        newGame: NewGame,
        shortcode: Shortcode,
    ): Game? {
        val insertedId =
            dsl
                .insertInto(GAMES)
                .set(GAMES.SHORTCODE, shortcode.value)
                .set(GAMES.SERIES_ID, newGame.seriesId.value)
                .set(GAMES.BLUE_TEAM_ID, newGame.blueTeamId.value)
                .set(GAMES.RED_TEAM_ID, newGame.redTeamId.value)
                .returning(GAMES.ID)
                .fetchOne()
                ?.get(GAMES.ID)
        return insertedId?.toGameId()?.let(::getById)
    }

    override fun insertResult(gameResult: GameResult): Game? {
        val insertedId =
            dsl
                .insertInto(GAME_RESULTS)
                .set(GAME_RESULTS.GAME_ID, gameResult.gameId.value)
                .set(GAME_RESULTS.WINNER_TEAM_ID, gameResult.winningTeamId.value)
                .set(GAME_RESULTS.LOSER_TEAM_ID, gameResult.losingTeamId.value)
                .returning(GAME_RESULTS.GAME_ID)
                .fetchOne()
                ?.get(GAME_RESULTS.GAME_ID)
        return insertedId?.toGameId()?.let(::getById)
    }

    override fun overwriteResult(gameResult: GameResult): Game? {
        val insertedId =
            dsl
                .update(GAME_RESULTS)
                .set(GAME_RESULTS.GAME_ID, gameResult.gameId.value)
                .set(GAME_RESULTS.WINNER_TEAM_ID, gameResult.winningTeamId.value)
                .set(GAME_RESULTS.LOSER_TEAM_ID, gameResult.losingTeamId.value)
                .where(GAME_RESULTS.GAME_ID.eq(gameResult.gameId.value))
                .returning(GAME_RESULTS.GAME_ID)
                .fetchOne()
                ?.get(GAME_RESULTS.GAME_ID)
        return insertedId?.toGameId()?.let(::getById)
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
                GAME_RESULTS.WINNER_TEAM_ID,
                GAME_RESULTS.LOSER_TEAM_ID,
            ).from(GAMES)
            .leftJoin(GAME_RESULTS)
            .on(GAMES.ID.eq(GAME_RESULTS.GAME_ID))

    fun rowToGame(row: Record): Game? {
        val gameId = row[GAMES.ID]?.toGameId() ?: return null
        val seriesId = row[GAMES.SERIES_ID]?.toSeriesId() ?: return null
        val blueTeamId = row[GAMES.BLUE_TEAM_ID]?.toTeamId() ?: return null
        val redTeamId = row[GAMES.RED_TEAM_ID]?.toTeamId() ?: return null
        val shortcode = row[GAMES.SHORTCODE]?.toShortcode() ?: return null
        val number = row[GAMES.NUMBER] ?: return null
        val winner = row[GAME_RESULTS.WINNER_TEAM_ID]?.toTeamId()
        val loser = row[GAME_RESULTS.LOSER_TEAM_ID]?.toTeamId()

        return Game(
            id = gameId,
            shortcode = shortcode,
            blueTeamId = blueTeamId,
            redTeamId = redTeamId,
            seriesId = seriesId,
            number = number,
            result = if (winner != null && loser != null) GameResult(gameId, winner, loser) else null,
        )
    }
}
