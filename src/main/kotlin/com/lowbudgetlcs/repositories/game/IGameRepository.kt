package com.lowbudgetlcs.repositories.game

import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.NewGame
import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.series.models.types.SeriesId

interface IGameRepository {
    fun getById(id: GameId): Game?

    fun getBySeriesId(id: SeriesId): List<Game>

    fun countBySeries(id: SeriesId): Int

    fun insert(newGame: NewGame): Game?

    fun recordResult(
        id: GameId,
        result: GameResult,
    ): Game?
}
