package com.lowbudgetlcs.dto

import com.lowbudgetlcs.models.Event
import com.lowbudgetlcs.models.EventStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class EventDTO(
    val id: Int,
    val name: String,
    val description: String,
    val riotTournamentId: Int,
    @Contextual val createdAt: OffsetDateTime,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime,
    val status: String
)

fun EventDTO.toModel(): Event = Event(
    id = id,
    name = name,
    description = description,
    riotTournamentId = riotTournamentId,
    createdAt = createdAt,
    startDate,
    endDate = endDate,
    status = EventStatus.valueOf(status)
)

fun Event.toDto(): EventDTO = EventDTO(
    id = id,
    name = name,
    description = description,
    riotTournamentId = riotTournamentId,
    createdAt = createdAt,
    startDate,
    endDate = endDate,
    status = status.name
)