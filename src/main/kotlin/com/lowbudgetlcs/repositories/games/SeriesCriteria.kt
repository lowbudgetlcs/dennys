package com.lowbudgetlcs.repositories.games

import com.lowbudgetlcs.entities.Game
import com.lowbudgetlcs.entities.SeriesId
import com.lowbudgetlcs.repositories.Criteria

class SeriesCriteria(val series: SeriesId) : Criteria<Game> {
    override fun meetCriteria(entities: List<Game>): List<Game> = entities.filter { it.series == series }
}