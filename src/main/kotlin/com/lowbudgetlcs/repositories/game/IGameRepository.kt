package com.lowbudgetlcs.repositories.game

import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.game.models.Game
import com.lowbudgetlcs.domain.game.models.NewGame
import com.lowbudgetlcs.domain.game.models.types.GameId

interface IGameRepository {
    fun getById(id: GameId): Game?

    fun insert(
        newGame: NewGame,
        shortcode: Shortcode,
    ): Game?
}
