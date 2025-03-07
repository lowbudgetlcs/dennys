package com.lowbudgetlcs.repositories.games

import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.models.SeriesId
import com.lowbudgetlcs.repositories.ICriteria

/**
 * Returns a list of games belonging to [series].
 */
class SeriesCriteria(val series: SeriesId) : ICriteria<Game> {
    override fun meetCriteria(entities: List<Game>): List<Game> = entities.filter { it.series == series }
}