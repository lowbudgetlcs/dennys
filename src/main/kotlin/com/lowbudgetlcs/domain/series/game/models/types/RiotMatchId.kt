package com.lowbudgetlcs.domain.series.game.models.types

const val RIOT_MATCH_ID_SEPARATOR = "_"

@JvmInline
value class RiotMatchId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "Riot match id cannot be blank." }
        require(value.count { it.toString() == RIOT_MATCH_ID_SEPARATOR } == 1) {
            "Riot match id must be '{platformId}${RIOT_MATCH_ID_SEPARATOR}{gameId}', got '$value'."
        }
        require(value.substringBefore(RIOT_MATCH_ID_SEPARATOR).isNotBlank()) {
            "Riot match id is missing a platformId, got '$value'."
        }
        require(value.substringAfter(RIOT_MATCH_ID_SEPARATOR).isNotBlank()) {
            "Riot match id is missing a gameId, got '$value'."
        }
    }
}
