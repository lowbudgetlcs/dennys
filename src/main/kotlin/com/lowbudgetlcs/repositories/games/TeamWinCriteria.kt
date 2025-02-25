package com.lowbudgetlcs.repositories.games

import com.lowbudgetlcs.entities.Game
import com.lowbudgetlcs.entities.TeamId
import com.lowbudgetlcs.repositories.ICriteria

/**
 * Returns a list of games where winner = [team].
 */
class TeamWinCriteria(private val team: TeamId) : ICriteria<Game> {
    override fun meetCriteria(entities: List<Game>): List<Game> = entities.filter { it.winner == team }
}