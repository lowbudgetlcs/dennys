package com.lowbudgetlcs.domain.event.routes.dto

import com.lowbudgetlcs.domain.event.models.enums.EventStage
import com.lowbudgetlcs.domain.event.models.enums.EventStatus


fun String.toEventStage(): EventStage = EventStage.entries.firstOrNull { it.name.equals(this, ignoreCase = true) }
    ?: throw IllegalArgumentException("Invalid event stage: '$this'")

fun String.toEventStatus(): EventStatus = EventStatus.entries.firstOrNull() { it.name.equals(this, ignoreCase = true) }
    ?: throw IllegalArgumentException("Invalid event status: '$this'")
