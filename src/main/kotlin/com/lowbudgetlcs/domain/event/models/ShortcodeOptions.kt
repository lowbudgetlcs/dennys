package com.lowbudgetlcs.domain.event.models

import com.lowbudgetlcs.domain.event.models.types.MapType
import com.lowbudgetlcs.domain.event.models.types.PickType

data class ShortcodeOptions(
    val metadata: String = "",
    val pickType: PickType = PickType.TOURNAMENT_DRAFT,
    val mapType: MapType = MapType.SUMMONERS_RIFT,
)
