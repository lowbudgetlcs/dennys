package com.lowbudgetlcs.entities

import kotlinx.serialization.Serializable


@Serializable
data class Division(override val id: DivisionId, val name: String, val tournamantId: Int) : Entity<DivisionId>

@Serializable
data class DivisionId(val id: Int)
