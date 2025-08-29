package com.lowbudgetlcs.api.dto.players

import com.lowbudgetlcs.domain.models.NewPlayer
import com.lowbudgetlcs.domain.models.PlayerName
import com.lowbudgetlcs.domain.models.PlayerWithAccounts
import com.lowbudgetlcs.api.dto.accounts.toDto

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