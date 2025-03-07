package com.lowbudgetlcs.repositories.games

import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.models.GameId
import com.lowbudgetlcs.repositories.IEntityRepository

interface IGameRepository : IEntityRepository<Game, GameId>