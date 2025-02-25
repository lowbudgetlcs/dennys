package com.lowbudgetlcs.repositories.games

import com.lowbudgetlcs.entities.Game
import com.lowbudgetlcs.entities.GameId
import com.lowbudgetlcs.repositories.IEntityRepository

interface IGameRepository : IEntityRepository<Game, GameId>