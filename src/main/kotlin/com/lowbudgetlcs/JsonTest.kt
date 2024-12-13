package com.lowbudgetlcs

import kotlinx.serialization.Serializable

@Serializable
data class JsonTest(
    val title: String, val count: Int,
)