package com.lowbudgetlcs.api.dto.players

import com.lowbudgetlcs.api.dto.teams.TeamDto
import com.lowbudgetlcs.domain.account.routes.dto.AccountDto
import kotlinx.serialization.Serializable

@Serializable
data class PlayerWithTeamsDto(
    val id: Int,
    val name: String,
    val accounts: List<AccountDto>,
    val teams: List<TeamDto>,
)
