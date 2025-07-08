package com.lowbudgetlcs.domain.models


data class Team(
    val id: Int,
    val name: String,
    val logoName: String?,
    val event: Event,
)
