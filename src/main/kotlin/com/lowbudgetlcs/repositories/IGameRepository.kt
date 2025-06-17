package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.models.GameId

interface IGameRepository {
    /* Fetch all Game records in storage */
    fun getAll(): List<Game>

    /* Fetch a single Game from storage, null if not found */
    fun get(id: GameId): Game?

    /* Update a Game in storage, returning the new object or null if the operation fails. */
    fun update(entity: Game): Game?
}