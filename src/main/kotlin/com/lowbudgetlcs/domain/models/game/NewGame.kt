package com.lowbudgetlcs.domain.models.game

data class NewGame(
    val shortCode: String,
    val blueSideId: Int,
    val redSideId: Int,
    val seriesId: Int,
)
