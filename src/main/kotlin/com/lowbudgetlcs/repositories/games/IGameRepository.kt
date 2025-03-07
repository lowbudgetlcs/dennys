package com.lowbudgetlcs.repositories.games

import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.models.GameId
import com.lowbudgetlcs.repositories.IRepository
import com.lowbudgetlcs.repositories.IUniqueRepository

interface IGameRepository : IUniqueRepository<Game, GameId>, IRepository<Game>