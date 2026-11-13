package com.lowbudgetlcs.domain.series.game

interface IRiotMatchIngestService {
    /** Fetches and stores Riot match data for games that lack it. A failing game is left for the next run. */
    suspend fun ingestPending()
}
