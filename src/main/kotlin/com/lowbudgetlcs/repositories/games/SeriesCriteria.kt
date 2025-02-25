package com.lowbudgetlcs.repositories.games

import com.lowbudgetlcs.entities.Game
import com.lowbudgetlcs.entities.SeriesId
import com.lowbudgetlcs.repositories.ICriteria

/**
 * Returns a list of games belonging to [series].
 */
class SeriesCriteria(val series: SeriesId) : ICriteria<Game> {
    override fun meetCriteria(entities: List<Game>): List<Game> = entities.filter { it.series == series }
}