package com.lowbudgetlcs.domain.player.models

import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.domain.player.models.types.PlayerName

data class Player(
    val id: PlayerId,
    val name: PlayerName,
)
