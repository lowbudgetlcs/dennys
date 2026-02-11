package com.lowbudgetlcs.domain.models.events

data class Shortcode(
    val value: String,
)

fun String.toShortcode(): Shortcode = Shortcode(this)

data class ShortcodeOptions(
    val metadata: String = "",
    val pickType: PickType = PickType.TOURNAMENT_DRAFT,
    val mapType: MapType = MapType.SUMMONERS_RIFT,
)
