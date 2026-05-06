package com.lowbudgetlcs.domain.division.core.event.model

import com.lowbudgetlcs.domain.division.core.event.model.enums.MapType
import com.lowbudgetlcs.domain.division.core.event.model.enums.PickType

data class ShortcodeOptions(
    val metadata: String = "",
    val pickType: PickType = PickType.TOURNAMENT_DRAFT,
    val mapType: MapType = MapType.SUMMONERS_RIFT,
)
