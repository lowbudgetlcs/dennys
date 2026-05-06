package com.lowbudgetlcs.gateways.riot.tournament

import com.lowbudgetlcs.domain.division.core.event.model.ShortcodeOptions

fun ShortcodeOptions.toShortcodeParametersDto(): RiotShortcodeParametersDto =
    RiotShortcodeParametersDto(
        mapType = mapType.name,
        pickType = pickType.name,
        metadata = metadata,
    )
