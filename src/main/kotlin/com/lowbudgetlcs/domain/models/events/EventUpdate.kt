package com.lowbudgetlcs.domain.models.events

import com.lowbudgetlcs.domain.Zeroable
import com.lowbudgetlcs.domain.models.events.group.EventGroupId
import java.time.Instant

data class EventUpdate(
    val name: String? = null,
    val description: String? = null,
    val startDate: Instant? = null,
    val endDate: Instant? = null,
    val status: EventStatus? = null,
    val eventGroupId: Zeroable<EventGroupId> = Zeroable(null, true),
)

fun Event.patch(update: EventUpdate): Event =
    copy(
        name = update.name ?: this.name,
        description = update.description ?: this.description,
        startDate = update.startDate ?: this.startDate,
        endDate = update.endDate ?: this.endDate,
        status = update.status ?: this.status,
        eventGroupId = if (update.eventGroupId.isZero) this.eventGroupId else update.eventGroupId.value,
    )
