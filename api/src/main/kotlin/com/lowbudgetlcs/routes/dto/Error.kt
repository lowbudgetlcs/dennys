package com.lowbudgetlcs.routes.dto

import kotlinx.serialization.Serializable

@Serializable
data class Error(val code: Int, val message: String)
