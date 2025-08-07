package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.NewTeam
import com.lowbudgetlcs.domain.models.Team
import com.lowbudgetlcs.domain.models.TeamId

interface ITeamRepository {
    fun insert(newTeam: NewTeam): Team
    fun getAll(): List<Team>
    fun getById(id: TeamId): Team
}