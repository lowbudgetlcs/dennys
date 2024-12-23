package com.lowbudgetlcs.routes.jsontest

import kotlinx.serialization.Serializable

@Serializable
data class TestJson(
    val title: String, val count: Int,
)
