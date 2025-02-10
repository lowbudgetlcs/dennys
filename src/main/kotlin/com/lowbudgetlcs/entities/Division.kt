package com.lowbudgetlcs.entities

import kotlinx.serialization.Serializable

@Serializable
data class DivisionId(val id: Int)

@Serializable
data class Division(val id: DivisionId, val name: String, val tournamantId: Int)