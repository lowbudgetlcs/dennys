package com.lowbudgetlcs.domain.event.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.event.core.model.enums.EventStage
import com.lowbudgetlcs.domain.event.core.model.enums.EventStatus


fun String.toEventStage(): EventStage = EventStage.entries.firstOrNull { it.name.equals(this, ignoreCase = true) }
    ?: throw IllegalArgumentException("Invalid event stage: '$this'")

fun String.toEventStatus(): EventStatus = EventStatus.entries.firstOrNull() { it.name.equals(this, ignoreCase = true) }
    ?: throw IllegalArgumentException("Invalid event status: '$this'")
