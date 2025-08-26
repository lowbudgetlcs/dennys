package com.lowbudgetlcs.routes.dto.riot.tournament

import com.lowbudgetlcs.domain.models.riot.tournament.NewShortcode

fun NewShortcode.toShortcodeParametersDto(): RiotShortcodeParametersDto = RiotShortcodeParametersDto(
    mapType = mapType.name,
    pickType = pickType.name,
    metadata = metadata,
)