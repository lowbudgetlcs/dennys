package com.lowbudgetlcs.domain.tournament

enum class PickType {
    TOURNAMENT_DRAFT, BLIND_PICK, DRAFT_MODE, ALL_RANDOM
}

enum class MapType {
    SUMMONERS_RIFT, HOWLING_ABYSS
}

data class Tournament(
    val id: Int,
    val metadata: String,
    val pickType: PickType,
    val mapType: MapType
)