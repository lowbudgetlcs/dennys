package com.lowbudgetlcs.repositories.game

import com.lowbudgetlcs.domain.models.Game
import com.lowbudgetlcs.domain.models.GameId
import com.lowbudgetlcs.domain.models.NewGame
import com.lowbudgetlcs.domain.models.SeriesId
import com.lowbudgetlcs.domain.models.riot.tournament.Shortcode

interface IGameRepository {
    fun getById(id: GameId): Game?

    fun insert(
        newGame: NewGame,
        shortcode: Shortcode,
        seriesId: SeriesId,
    ): Game?
}
