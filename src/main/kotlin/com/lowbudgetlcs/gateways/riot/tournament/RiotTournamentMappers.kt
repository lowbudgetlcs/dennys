package com.lowbudgetlcs.gateways.riot.tournament

import com.lowbudgetlcs.domain.division.core.model.ShortcodeOptions

fun ShortcodeOptions.toShortcodeParametersDto(): RiotShortcodeParametersDto =
    RiotShortcodeParametersDto(
        mapType = mapType.name,
        pickType = pickType.name,
        metadata = metadata,
    )
