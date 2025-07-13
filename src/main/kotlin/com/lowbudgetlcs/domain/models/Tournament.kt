package com.lowbudgetlcs.domain.models

@JvmInline
value class ShortCode(val value: String)

@JvmInline
value class TournamentId(val value: Int)

data class Tournament(val id: TournamentId, val name: String)

data class NewTournament(val name: String)

enum class PickType {
    TOURNAMENT_DRAFT, BLIND_PICK, DRAFT_MODE, ALL_RANDOM
}

enum class MapType {
    SUMMONERS_RIFT, HOWLING_ABYSS
}

data class TournamentCode(
    val id: ShortCode,
    val metadata: String,
    val pickType: PickType,
    val mapType: MapType
)

data class NewTournamentCode(
    val metadata: String = "",
    val pickType: PickType = PickType.TOURNAMENT_DRAFT,
    val mapType: MapType = MapType.SUMMONERS_RIFT
)