package com.lowbudgetlcs.routes.api.v1.jsontest

import kotlinx.serialization.Serializable

@Serializable
data class TestJson(
    val title: String, val count: Int,
)
