package com.lowbudgetlcs.domain.series.core.port

import com.lowbudgetlcs.domain.event.core.model.types.Shortcode
import com.lowbudgetlcs.domain.series.core.model.Game
import com.lowbudgetlcs.domain.series.core.model.NewGame
import com.lowbudgetlcs.domain.series.core.model.types.GameId

interface IGameRepository {
    fun getById(id: GameId): Game?

    fun insert(
        newGame: NewGame,
        shortcode: Shortcode,
    ): Game?
}
