package com.lowbudgetlcs.domain.division.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.division.core.model.enums.EventStage
import com.lowbudgetlcs.domain.division.core.model.enums.EventStatus


fun String.toEventStage(): EventStage = EventStage.entries.firstOrNull { it.name.equals(this, ignoreCase = true) }
    ?: throw IllegalArgumentException("Invalid event stage: '$this'")

fun String.toEventStatus(): EventStatus = EventStatus.entries.firstOrNull() { it.name.equals(this, ignoreCase = true) }
    ?: throw IllegalArgumentException("Invalid event status: '$this'")
