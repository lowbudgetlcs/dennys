package com.lowbudgetlcs.domain.tournament


data class NewTournament(
    val metadata: String = "",
    val pickType: PickType = PickType.TOURNAMENT_DRAFT,
    val mapType: MapType = MapType.SUMMONERS_RIFT
)
