package com.lowbudgetlcs.gateways.riot.tournament

import com.lowbudgetlcs.domain.event.models.ShortcodeOptions
import kotlinx.serialization.json.Json

fun ShortcodeOptions.toShortcodeParametersDto(): RiotShortcodeParametersDto =
    RiotShortcodeParametersDto(
        mapType = mapType.name,
        pickType = pickType.name,
        metadata = Json.encodeToString(metadata),
    )
