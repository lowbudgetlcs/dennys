package com.lowbudgetlcs.gateways.riot.tournament

import com.lowbudgetlcs.domain.event.models.ShortcodeOptions

fun ShortcodeOptions.toShortcodeParametersDto(): RiotShortcodeParametersDto =
    RiotShortcodeParametersDto(
        mapType = mapType.name,
        pickType = pickType.name,
        metadata = metadata,
    )
