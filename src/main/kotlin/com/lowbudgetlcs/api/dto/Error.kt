package com.lowbudgetlcs.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class Error(
    val code: Int,
    val message: String = "An error has occured.",
)
