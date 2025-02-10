package com.lowbudgetlcs.repositories.games

import com.lowbudgetlcs.entities.Game
import com.lowbudgetlcs.repositories.Criteria

class ShortcodeCriteria(val shortcode: String) : Criteria<Game> {
    override fun meetCriteria(entities: List<Game>): List<Game> = entities.filter { it.shortCode == shortcode }
}