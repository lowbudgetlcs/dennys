package com.lowbudgetlcs.domain.models.series

data class NewSeries(
    val eventId: Int,
    val gamesToWin: Int,
    val participantIds: List<Int>,
)