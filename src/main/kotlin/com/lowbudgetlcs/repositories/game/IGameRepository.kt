package com.lowbudgetlcs.repositories.game

import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.models.Game
import com.lowbudgetlcs.domain.models.GameId
import com.lowbudgetlcs.domain.models.NewGame

interface IGameRepository {
    fun getById(id: GameId): Game?

    fun insert(
        newGame: NewGame,
        shortcode: Shortcode,
    ): Game?
}
