package com.lowbudgetlcs.domain.game.models

import com.lowbudgetlcs.domain.game.models.types.GameId

// Type Extensions
fun Int.toGameId(): GameId = GameId(this)
