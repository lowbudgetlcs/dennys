package com.lowbudgetlcs.domain.models.player

data class Player(
    val id: Int, val name: String, val accountIds: List<Int>, val teamId: Int?, val eventId: Int?
)