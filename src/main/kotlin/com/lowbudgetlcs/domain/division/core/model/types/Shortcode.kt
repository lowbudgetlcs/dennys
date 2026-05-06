package com.lowbudgetlcs.domain.division.core.model.types

@JvmInline
value class Shortcode(
    val value: String,
)

// Extensions
fun String.toShortcode(): Shortcode = Shortcode(this)
