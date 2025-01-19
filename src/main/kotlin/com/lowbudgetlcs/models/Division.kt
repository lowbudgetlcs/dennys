package com.lowbudgetlcs.models

import kotlinx.serialization.Serializable

@Serializable
data class Division(val id: DivisionId, val name: String, val tournamantId: Int)

@Serializable
data class DivisionId(val id: Int)
