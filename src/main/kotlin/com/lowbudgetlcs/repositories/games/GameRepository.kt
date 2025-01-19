package com.lowbudgetlcs.repositories.games

import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.models.GameId
import com.lowbudgetlcs.repositories.Repository

interface GameRepository : Repository<Game, GameId> {
}