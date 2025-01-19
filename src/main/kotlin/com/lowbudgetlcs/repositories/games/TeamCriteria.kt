package com.lowbudgetlcs.repositories.games

import com.lowbudgetlcs.models.Game
import com.lowbudgetlcs.models.TeamId
import com.lowbudgetlcs.repositories.Criteria

class TeamWinCriteria(val team: TeamId) : Criteria<Game> {
    override fun meetCriteria(entities: List<Game>): List<Game> = entities.filter { it.winner == team }
}