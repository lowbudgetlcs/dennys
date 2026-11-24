package com.lowbudgetlcs.gateways.riot.match

import com.lowbudgetlcs.domain.series.game.models.types.RiotMatchId
import com.lowbudgetlcs.gateways.riot.RiotApiException
import kotlinx.serialization.json.JsonObject

interface IRiotMatchGateway {
    /**
     * Returns Riot's match-v5 body unchanged.
     *
     * @throws NoSuchElementException if Riot has no match for the id (404)
     * @throws RiotApiException for other Riot API failures
     */
    suspend fun getMatch(matchId: RiotMatchId): JsonObject

    /**
     * Returns Riot's match-v5 timeline body unchanged.
     *
     * @throws NoSuchElementException if Riot has no timeline for the id (404)
     * @throws RiotApiException for other Riot API failures
     */
    suspend fun getTimeline(matchId: RiotMatchId): JsonObject
}
