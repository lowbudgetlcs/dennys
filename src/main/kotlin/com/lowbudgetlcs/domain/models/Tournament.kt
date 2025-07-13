package com.lowbudgetlcs.domain.models

@JvmInline
value class TournamentId(val value: Int)

enum class PickType {
    TOURNAMENT_DRAFT, BLIND_PICK, DRAFT_MODE, ALL_RANDOM
}

enum class MapType {
    SUMMONERS_RIFT, HOWLING_ABYSS
}

data class Tournament(
    val id: TournamentId,
    val metadata: String,
    val pickType: PickType,
    val mapType: MapType
)

data class NewTournament(
    val metadata: String = "",
    val pickType: PickType = PickType.TOURNAMENT_DRAFT,
    val mapType: MapType = MapType.SUMMONERS_RIFT
)