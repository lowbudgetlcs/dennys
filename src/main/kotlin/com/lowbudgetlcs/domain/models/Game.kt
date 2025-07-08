package com.lowbudgetlcs.domain.models

data class Game(
    val id: Int,
    val shortCode: String,
    val blueSideId: Int,
    val redSideId: Int,
    val seriesId: Int,
    val winnerId: Int?,
    val loserId: Int?
)