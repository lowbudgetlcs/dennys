package com.lowbudgetlcs.domain.models

data class Series(
    val id: Int,
    val eventId: Int,
    val gamesToWin: Int,
    val participantIds: List<Int>,
    val winnerId: Int?,
    val loserId: Int?
)