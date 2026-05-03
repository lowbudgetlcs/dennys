package com.lowbudgetlcs.api.plugins

import kotlinx.serialization.Serializable

@Serializable
data class Error(
    val code: Int,
    val message: String = "An error has occured.",
)
