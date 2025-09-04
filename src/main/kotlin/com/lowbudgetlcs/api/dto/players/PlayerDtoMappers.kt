package com.lowbudgetlcs.api.dto.players

import com.lowbudgetlcs.api.dto.accounts.toDto
import com.lowbudgetlcs.domain.models.player.NewPlayer
import com.lowbudgetlcs.domain.models.player.PlayerName
import com.lowbudgetlcs.domain.models.player.PlayerWithAccounts

fun NewPlayerDto.toNewPlayer(): NewPlayer =
    NewPlayer(
        name = PlayerName(name)
    )

fun PlayerWithAccounts.toDto(): PlayerDto =
    PlayerDto(
        id = id.value,
        name = name.value,
        accounts = accounts.map { it.toDto() }
    )