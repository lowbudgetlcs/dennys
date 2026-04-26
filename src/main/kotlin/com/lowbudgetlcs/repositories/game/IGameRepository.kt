package com.lowbudgetlcs.repositories.game

import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.NewGame
import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.series.models.types.SeriesId

interface IGameRepository {
    fun getById(id: GameId): Game?

    fun getBySeriesId(id: SeriesId): List<Game>

    fun getByShortcode(shortcode: Shortcode): Game?

    fun insert(
        newGame: NewGame,
        shortcode: Shortcode,
    ): Game?

    fun insertGameResult(gameResult: GameResult): Game?
}
