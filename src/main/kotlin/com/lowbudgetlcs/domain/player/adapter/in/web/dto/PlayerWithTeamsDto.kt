package com.lowbudgetlcs.domain.player.adapter.`in`.web.dto

import com.lowbudgetlcs.api.dto.teams.TeamDto
import com.lowbudgetlcs.api.dto.teams.toDto
import com.lowbudgetlcs.domain.account.adapter.`in`.web.dto.AccountDto
import com.lowbudgetlcs.domain.player.core.model.PlayerWithTeams
import kotlinx.serialization.Serializable

@Serializable
data class PlayerWithTeamsDto(
    val id: Int,
    val name: String,
    val accounts: List<AccountDto>,
    val teams: List<TeamDto>,
)

// Extensions
fun PlayerWithTeams.toDto(): PlayerWithTeamsDto = PlayerWithTeamsDto(
    id = id.value, name = name.value, accounts = listOf(), teams = teams.map { it.toDto() })
