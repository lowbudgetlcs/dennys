package com.lowbudgetlcs.domain.series.game.models

import com.lowbudgetlcs.domain.series.game.models.types.GameId
import kotlinx.serialization.json.JsonObject
import java.time.Instant

/** Riot's match-v5 payloads for one LBLCS [Game]. A Riot "match" is a single game, never a series. */
data class RiotMatchData(
    val gameId: GameId,
    val match: JsonObject,
    val timeline: JsonObject?,
    val fetchedAt: Instant,
)
