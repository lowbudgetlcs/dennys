package com.lowbudgetlcs.domain.models.team

import com.lowbudgetlcs.domain.models.event.Event

data class Team(
    val id: Int,
    val name: String,
    val logoName: String?,
    val eventId: Int?,
)