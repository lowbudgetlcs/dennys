package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.Dennys
import com.lowbudgetlcs.models.*
import kotlinx.serialization.json.Json
import migrations.Games

class DatabaseGameRepository(private val lblcs: Dennys) : IGameRepository {

    /**
     * Returns a [Game] derived from [Games].
     */
    private fun Games.toGame(): Game = Game(
        GameId(this.id),
        this.shortcode,
        this.game_num,
        this.winner_id?.let { TeamId(it) },
        this.loser_id?.let { TeamId(it) },
        this.callback_result?.let { Json.decodeFromString<PostMatchCallback>(it) },
        this.created_at,
        SeriesId(this.series_id)
    )

    override fun getAll(): List<Game> = lblcs.gamesQueries.readAll().executeAsList().map { it.toGame() }

    override fun get(id: GameId): Game? = lblcs.gamesQueries.readById(id.id).executeAsOneOrNull()?.toGame()
    override fun get(shortcode: String): Game? =
        lblcs.gamesQueries.readByShortcode(shortcode).executeAsOneOrNull()?.toGame()

    override fun get(
        team: TeamId,
        series: SeriesId
    ): List<Game> = lblcs.gamesQueries.winsInSeriesByTeam(series_id = series.id, winner_id = team.id).executeAsList()
        .map { it.toGame() }

    override fun update(entity: Game): Game? = lblcs.gamesQueries.updateGame(
        winner_id = entity.winner?.id,
        loser_id = entity.loser?.id,
        callback_result = Json.encodeToString(entity.callbackResult),
        id = entity.id.id
    ).executeAsOneOrNull()?.toGame()
}
