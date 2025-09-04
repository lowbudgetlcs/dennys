package com.lowbudgetlcs.domain.services.game

import com.lowbudgetlcs.domain.models.Game
import com.lowbudgetlcs.domain.models.NewGame

interface IGameService {
    suspend fun createGame(newGame: NewGame): Game
}