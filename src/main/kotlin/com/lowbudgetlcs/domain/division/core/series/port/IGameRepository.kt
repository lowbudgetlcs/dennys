package com.lowbudgetlcs.domain.division.core.series.port

import com.lowbudgetlcs.domain.division.core.event.model.types.Shortcode
import com.lowbudgetlcs.domain.division.core.series.model.Game
import com.lowbudgetlcs.domain.division.core.series.model.NewGame
import com.lowbudgetlcs.domain.division.core.series.model.types.GameId

interface IGameRepository {
    suspend fun getById(id: GameId): Game?
    suspend fun insert(
        newGame: NewGame,
        shortcode: Shortcode,
    ): Game?
}
