package com.lowbudgetlcs.api.dto.players

import com.lowbudgetlcs.domain.models.player.NewPlayer
import com.lowbudgetlcs.domain.models.player.Player
import com.lowbudgetlcs.domain.models.player.PlayerName

fun NewPlayerDto.toNewPlayer(): NewPlayer =
    NewPlayer(
        name = PlayerName(name),
    )

// TODO: Remove accounts from API contracts, or add PlayerWithAccounts back.
fun Player.toDto(): PlayerDto =
    PlayerDto(
        id = id.value,
        name = name.value,
        accounts = listOf(),
    )
