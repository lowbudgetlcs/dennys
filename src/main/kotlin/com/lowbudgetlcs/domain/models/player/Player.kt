package com.lowbudgetlcs.domain.models.player

data class Player(
    val id: Int, val name: String, val teamId: Int?, val eventId: Int?
)