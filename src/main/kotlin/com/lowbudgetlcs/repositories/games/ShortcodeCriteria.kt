package com.lowbudgetlcs.repositories.games

import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.repositories.ICriteria

/**
 * Returns a list of games with a matching [shortcode].
 */
class ShortcodeCriteria(val shortcode: String) : ICriteria<Game> {
    override fun meetCriteria(entities: List<Game>): List<Game> = entities.filter { it.shortCode == shortcode }
}