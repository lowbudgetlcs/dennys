package com.lowbudgetlcs.domain.event.models.types

@JvmInline
value class Shortcode(
    val value: String,
)

// Extensions
fun String.toShortcode(): Shortcode = Shortcode(this)
