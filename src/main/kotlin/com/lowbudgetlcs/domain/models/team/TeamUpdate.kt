package com.lowbudgetlcs.domain.models.team

import com.lowbudgetlcs.domain.Zeroable
import com.lowbudgetlcs.domain.event.models.types.EventId

data class TeamUpdate(
    val name: TeamName? = null,
    val logoName: TeamLogoName? = null,
    val eventId: Zeroable<EventId> = Zeroable(null, true),
)

fun Team.patch(update: TeamUpdate): Team =
    copy(
        name = update.name ?: this.name,
        logoName = update.logoName ?: this.logoName,
        eventId = if (update.eventId.isZero) this.eventId else update.eventId.value,
    )
