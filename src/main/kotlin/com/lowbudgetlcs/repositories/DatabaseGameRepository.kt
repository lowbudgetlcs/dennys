package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.Database
import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.models.GameId
import com.lowbudgetlcs.models.SeriesId
import com.lowbudgetlcs.models.TeamId
import com.lowbudgetlcs.routes.riot.RiotCallback
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import migrations.Games

class DatabaseGameRepository(private val lblcs: Database) : IGameRepository {

    /**
     * Returns a [Game] derived from [Games].
     */
    private fun Games.toGame(): Game = Game(
        GameId(this.id),
        this.shortcode,
        this.game_num,
        this.winner_id?.let { TeamId(it) },
        this.loser_id?.let { TeamId(it) },
        this.callback_result?.let { Json.decodeFromString<RiotCallback>(it) },
        this.created_at,
        SeriesId(this.series_id)
    )

    override fun getAll(): List<Game> = lblcs.gamesQueries.readAll().executeAsList().map { it.toGame() }

    override fun get(id: GameId): Game? = lblcs.gamesQueries.readById(id.id).executeAsOneOrNull()?.toGame()

    override fun update(entity: Game): Game? = lblcs.gamesQueries.updateGame(
        winner_id = entity.winner?.id,
        loser_id = entity.loser?.id,
        callback_result = Json.encodeToString(entity.callbackResult),
        id = entity.id.id
    ).executeAsOneOrNull()?.toGame()
}
