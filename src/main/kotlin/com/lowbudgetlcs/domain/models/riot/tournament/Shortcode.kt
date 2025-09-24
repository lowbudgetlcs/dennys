package com.lowbudgetlcs.domain.models.riot.tournament

data class Shortcode(
    val value: String,
)

fun String.toShortcode(): Shortcode = Shortcode(this)

data class NewShortcode(
    val metadata: String = "",
    val pickType: PickType = PickType.TOURNAMENT_DRAFT,
    val mapType: MapType = MapType.SUMMONERS_RIFT,
)
