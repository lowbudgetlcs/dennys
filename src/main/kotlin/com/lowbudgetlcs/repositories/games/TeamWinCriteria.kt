package com.lowbudgetlcs.repositories.games

import com.lowbudgetlcs.entities.Game
import com.lowbudgetlcs.entities.TeamId
import com.lowbudgetlcs.repositories.Criteria

class TeamWinCriteria(private val team: TeamId) : Criteria<Game> {
    override fun meetCriteria(entities: List<Game>): List<Game> = entities.filter { it.winner == team }
}