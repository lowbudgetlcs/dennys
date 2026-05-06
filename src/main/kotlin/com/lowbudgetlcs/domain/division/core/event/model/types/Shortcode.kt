package com.lowbudgetlcs.domain.division.core.event.model.types

@JvmInline
value class Shortcode(
    val value: String,
)

// Extensions
fun String.toShortcode(): Shortcode = Shortcode(this)
