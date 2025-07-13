package com.lowbudgetlcs.dto.events

import com.lowbudgetlcs.domain.models.Event
import com.lowbudgetlcs.domain.models.NewEvent
import com.lowbudgetlcs.domain.models.NewTournament

fun CreateEventDto.toNewTournament(): NewTournament = NewTournament(
    metadata = metadata,
    pickType = pickType,
    mapType = mapType
)

fun CreateEventDto.toNewEvent(): NewEvent = NewEvent(
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
    tournamentId = tournamentId.value,
    status = status,
)