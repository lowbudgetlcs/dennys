package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.models.GameId
import com.lowbudgetlcs.models.SeriesId
import com.lowbudgetlcs.models.TeamId

interface IGameRepository {
    /* Fetch all Game records in storage */
    fun getAll(): List<Game>

    /* Fetch a single Game from storage, null if not found */
    fun get(id: GameId): Game?

    /* Fetch a single game shortcode from storage, null if not found */
    fun get(shortcode: String): Game?

    fun get(team: TeamId, series: SeriesId): List<Game>

    /* Update a Game in storage, returning the new object or null if the operation fails. */
    fun update(entity: Game): Game?
}