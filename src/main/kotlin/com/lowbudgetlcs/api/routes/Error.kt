package com.lowbudgetlcs.api.routes

import kotlinx.serialization.Serializable

@Serializable
data class Error(
    val code: Int,
    val message: String = "An error has occured.",
)
