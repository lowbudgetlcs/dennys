package com.lowbudgetlcs.domain.team.models

import com.lowbudgetlcs.domain.team.models.types.TeamLogoName
import com.lowbudgetlcs.domain.team.models.types.TeamName

data class NewTeam(
    val name: TeamName,
    val logoName: String? = null,
)
