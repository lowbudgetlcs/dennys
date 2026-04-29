package com.lowbudgetlcs.domain.event.models

data class Shortcode(
    val value: String,
)

// Extensions
fun String.toShortcode(): Shortcode = Shortcode(this)
