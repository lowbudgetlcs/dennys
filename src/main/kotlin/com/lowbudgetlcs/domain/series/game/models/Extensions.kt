package com.lowbudgetlcs.domain.series.game.models

import com.lowbudgetlcs.domain.series.game.models.types.GameId

// Type Extensions
fun Int.toGameId(): GameId = GameId(this)

// Filter Extensions
fun List<Game>.filterCompleted(): List<Game> = this.filter { it.result != null }
