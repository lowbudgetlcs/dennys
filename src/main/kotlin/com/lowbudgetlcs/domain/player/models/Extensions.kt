package com.lowbudgetlcs.domain.player.models

import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.domain.player.models.types.PlayerName

// Type Extensions
fun Int.toPlayerId(): PlayerId = PlayerId(this)

fun String.toPlayerName(): PlayerName = PlayerName(this)

// Class Extensions
fun NewPlayer.toPlayer(id: PlayerId): Player =
    Player(
        id = id,
        name = name,
    )
