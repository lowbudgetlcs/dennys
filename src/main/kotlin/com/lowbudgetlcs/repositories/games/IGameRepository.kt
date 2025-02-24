package com.lowbudgetlcs.repositories.games

import com.lowbudgetlcs.entities.Game
import com.lowbudgetlcs.entities.GameId
import com.lowbudgetlcs.repositories.IRepository

interface IGameRepository : IRepository<Game, GameId>