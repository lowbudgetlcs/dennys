package com.lowbudgetlcs.domain.event.models

data class ShortcodeOptions(
    val metadata: String = "",
    val pickType: PickType = PickType.TOURNAMENT_DRAFT,
    val mapType: MapType = MapType.SUMMONERS_RIFT,
)
