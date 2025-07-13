package com.lowbudgetlcs.dto.events

import com.lowbudgetlcs.domain.models.Event
import com.lowbudgetlcs.domain.models.NewEvent

fun CreateEventDto.toDomain(): NewEvent = NewEvent(
    name = name,
    description = description,
    startDate = startDate,
    endDate = endDate,
    status = status
)

fun Event.toDto(): EventDto = EventDto(
    id = id.value,
    name = name,
    startDate = startDate,
    endDate = endDate,
    createdAt = createdAt,
    description = description,
    riotTournamentId = riotTournamentId.value,
    status = status,
)