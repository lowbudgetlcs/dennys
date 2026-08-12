package com.lowbudgetlcs.api.dto.players

import com.lowbudgetlcs.api.dto.accounts.toDto
import com.lowbudgetlcs.api.dto.teams.toDto
import com.lowbudgetlcs.domain.player.models.NewPlayer
import com.lowbudgetlcs.domain.player.models.Player
import com.lowbudgetlcs.domain.player.models.PlayerWithAccounts
import com.lowbudgetlcs.domain.player.models.PlayerWithTeams
import com.lowbudgetlcs.domain.player.models.types.PlayerName

fun NewPlayerDto.toNewPlayer(): NewPlayer = NewPlayer(
    name = PlayerName(name),
)

// Reports no accounts because it has no way to know them. Correct for a freshly created player, but
// it is also how players embedded in team responses are rendered, where accounts are silently
// dropped — see the follow-up issue referenced in the PR.
fun Player.toDto(): PlayerDto = PlayerDto(
    id = id.value,
    name = name.value,
    accounts = listOf(),
)

fun PlayerWithAccounts.toDto(): PlayerDto = PlayerDto(
    id = id.value,
    name = name.value,
    accounts = accounts.map { it.toDto() },
)

fun PlayerWithTeams.toDto(): PlayerWithTeamsDto = PlayerWithTeamsDto(
    id = id.value,
    name = name.value,
    accounts = accounts.map { it.toDto() },
    teams = teams.map { it.toDto() })
