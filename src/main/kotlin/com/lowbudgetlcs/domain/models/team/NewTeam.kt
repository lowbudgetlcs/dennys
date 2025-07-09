package com.lowbudgetlcs.domain.models.team

import com.lowbudgetlcs.domain.models.event.Event

data class NewTeam(
    val name: String,
    val logoName: String?,
    val eventId: Int?,
)