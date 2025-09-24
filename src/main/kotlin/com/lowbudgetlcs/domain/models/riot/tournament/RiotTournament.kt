package com.lowbudgetlcs.domain.models.riot.tournament

@JvmInline
value class RiotTournamentId(
    val value: Int,
)

fun Int.toRiotTournamentId(): RiotTournamentId = RiotTournamentId(this)

data class RiotTournament(
    val id: RiotTournamentId,
    val name: String,
)

data class NewRiotTournament(
    val name: String,
)

enum class PickType {
    TOURNAMENT_DRAFT,
    BLIND_PICK,
    DRAFT_MODE,
    ALL_RANDOM,
}

enum class MapType {
    SUMMONERS_RIFT,
    HOWLING_ABYSS,
}
