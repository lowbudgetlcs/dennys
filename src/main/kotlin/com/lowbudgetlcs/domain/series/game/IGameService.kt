package com.lowbudgetlcs.domain.series.game

import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.series.game.models.Game
import com.lowbudgetlcs.domain.series.game.models.GameResult
import com.lowbudgetlcs.domain.series.game.models.NewGame

interface IGameService {
    fun getByShortcode(shortcode: Shortcode): Game

    /**
     * Create a game inside of a series.
     *
     * @param NewGame the new game parameters.
     */
    suspend fun createGame(newGame: NewGame): Game

    /**
     * Process a game result.
     *
     * @param GameResult the game result to be processed.
     */
    fun completeGame(result: GameResult): Game
}
