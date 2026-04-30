package com.lowbudgetlcs.domain.player.core.model

import com.lowbudgetlcs.domain.player.core.model.types.PlayerId
import com.lowbudgetlcs.domain.player.core.model.types.PlayerName

data class NewPlayer(
    val name: PlayerName,
)

// Extensions
fun NewPlayer.toPlayer(id: PlayerId): Player =
    Player(
        id = id,
        name = name,
    )
