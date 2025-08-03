package com.lowbudgetlcs.routes.dto.players

import com.lowbudgetlcs.domain.models.NewPlayer
import com.lowbudgetlcs.domain.models.PlayerName
import com.lowbudgetlcs.domain.models.PlayerWithAccounts
import com.lowbudgetlcs.domain.models.toTeamId
import com.lowbudgetlcs.routes.dto.accounts.toDto

fun NewPlayerDto.toNewPlayer(): NewPlayer =
    NewPlayer(
        name = PlayerName(name),
        teamId = teamId?.toTeamId()
    )

fun PlayerWithAccounts.toDto(): PlayerDto =
    PlayerDto(
        id = id.value,
        name = name.name,
        accounts = accounts.map { it.toDto() }
    )