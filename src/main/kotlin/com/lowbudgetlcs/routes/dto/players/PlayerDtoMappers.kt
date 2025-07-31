package com.lowbudgetlcs.routes.dto.players

import com.lowbudgetlcs.domain.models.*

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

fun RiotAccount.toDto(): RiotAccountDto =
    RiotAccountDto(
        id = id.value,
        riotPuuid = riotPuuid.value,
        playerId = playerId?.value
    )