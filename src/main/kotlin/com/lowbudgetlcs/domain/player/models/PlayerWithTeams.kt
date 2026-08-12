package com.lowbudgetlcs.domain.player.models

import com.lowbudgetlcs.domain.account.models.Account
import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.domain.player.models.types.PlayerName
import com.lowbudgetlcs.domain.team.models.Team

data class PlayerWithTeams(
    val id: PlayerId,
    val name: PlayerName,
    val accounts: List<Account>,
    val teams: List<Team>,
)
