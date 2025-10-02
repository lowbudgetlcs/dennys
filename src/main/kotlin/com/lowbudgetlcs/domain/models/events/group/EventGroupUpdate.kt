package com.lowbudgetlcs.domain.models.events.group

data class EventGroupUpdate(
    val name: String? = null,
)

fun EventGroup.patch(update: EventGroupUpdate): EventGroup =
    EventGroup(
        id = id,
        name = update.name ?: this.name,
    )
