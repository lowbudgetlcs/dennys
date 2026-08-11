package com.lowbudgetlcs.repositories.game

import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.NewGame
import com.lowbudgetlcs.domain.series.game.models.toGameId
import com.lowbudgetlcs.domain.series.game.models.toRiotMatchId
import com.lowbudgetlcs.domain.series.game.models.toTournamentCodeId
import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.series.models.toSeriesId
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.toTeamId
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL.max
import org.jooq.storage.tables.references.GAMES
import org.jooq.storage.tables.references.GAME_RESULTS

class GameRepository(
    private val dsl: DSLContext,
) : IGameRepository {
    override fun getById(id: GameId): Game? = selectGames().where(GAMES.ID.eq(id.value)).fetchOne()?.let(::rowToGame)

    override fun getBySeriesId(id: SeriesId): List<Game> =
        selectGames()
            .where(GAMES.SERIES_ID.eq(id.value))
            .orderBy(GAMES.NUMBER)
            .fetch()
            .mapNotNull(::rowToGame)

    override fun countBySeries(id: SeriesId): Int = dsl.fetchCount(GAMES, GAMES.SERIES_ID.eq(id.value))

    override fun insert(newGame: NewGame): Game? {
        val insertedId =
            dsl.transactionResult { t ->
                val tx = t.dsl()
                tx.execute("SELECT pg_advisory_xact_lock(?)", newGame.seriesId.value.toLong())
                newGame.riotMatchId?.let { matchId ->
                    val existing =
                        tx
                            .select(GAMES.ID)
                            .from(GAMES)
                            .where(GAMES.RIOT_MATCH_ID.eq(matchId.value))
                            .fetchOne()
                            ?.get(GAMES.ID)
                    check(existing == null) {
                        "Riot match '${matchId.value}' is already recorded as game '$existing'."
                    }
                }
                val highest =
                    tx
                        .select(max(GAMES.NUMBER))
                        .from(GAMES)
                        .where(GAMES.SERIES_ID.eq(newGame.seriesId.value))
                        .fetchOne()
                        ?.value1() ?: 0
                val number = highest + 1
                val gameId =
                    tx
                        .insertInto(GAMES)
                        .set(GAMES.SERIES_ID, newGame.seriesId.value)
                        .set(GAMES.TOURNAMENT_CODE_ID, newGame.tournamentCodeId?.value)
                        .set(GAMES.RIOT_MATCH_ID, newGame.riotMatchId?.value)
                        .set(GAMES.NUMBER, number)
                        .returning(GAMES.ID)
                        .fetchOne()
                        ?.get(GAMES.ID)
                newGame.result?.let { insertResult(tx, gameId, it) }
                gameId
            }
        return insertedId?.toGameId()?.let(::getById)
    }

    override fun recordResult(
        id: GameId,
        result: GameResult,
    ): Game? {
        insertResult(dsl, id.value, result)
        return getById(id)
    }

    private fun insertResult(
        ctx: DSLContext,
        gameId: Int?,
        result: GameResult,
    ) {
        ctx
            .insertInto(GAME_RESULTS)
            .set(GAME_RESULTS.GAME_ID, gameId)
            .set(GAME_RESULTS.WINNER_TEAM_ID, result.winningTeamId.value)
            .set(GAME_RESULTS.LOSER_TEAM_ID, result.losingTeamId.value)
            .execute()
    }

    private fun selectGames() =
        dsl
            .select(
                GAMES.ID,
                GAMES.SERIES_ID,
                GAMES.TOURNAMENT_CODE_ID,
                GAMES.RIOT_MATCH_ID,
                GAMES.NUMBER,
                GAMES.CREATED_AT,
                GAME_RESULTS.WINNER_TEAM_ID,
                GAME_RESULTS.LOSER_TEAM_ID,
            ).from(GAMES)
            .leftJoin(GAME_RESULTS)
            .on(GAMES.ID.eq(GAME_RESULTS.GAME_ID))

    private fun rowToGame(row: Record): Game? {
        val id = row[GAMES.ID]?.toGameId() ?: return null
        val seriesId = row[GAMES.SERIES_ID]?.toSeriesId() ?: return null
        val number = row[GAMES.NUMBER] ?: return null
        val createdAt = row[GAMES.CREATED_AT] ?: return null
        val winner = row[GAME_RESULTS.WINNER_TEAM_ID]?.toTeamId()
        val loser = row[GAME_RESULTS.LOSER_TEAM_ID]?.toTeamId()

        return Game(
            id = id,
            seriesId = seriesId,
            tournamentCodeId = row[GAMES.TOURNAMENT_CODE_ID]?.toTournamentCodeId(),
            riotMatchId = row[GAMES.RIOT_MATCH_ID]?.toRiotMatchId(),
            number = number,
            createdAt = createdAt,
            result = if (winner != null && loser != null) GameResult(winner, loser) else null,
        )
    }
}
