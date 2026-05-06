package com.lowbudgetlcs.domain.division.core.event.port

import com.lowbudgetlcs.domain.division.core.event.model.Event
import com.lowbudgetlcs.domain.division.core.event.model.EventUpdate
import com.lowbudgetlcs.domain.division.core.event.model.NewEvent
import com.lowbudgetlcs.domain.division.core.event.model.RiotTournamentId
import com.lowbudgetlcs.domain.division.core.event.model.types.EventGroupId
import com.lowbudgetlcs.domain.division.core.event.model.types.EventId
import com.lowbudgetlcs.domain.division.core.event.model.types.EventName

interface IEventRepository {
    suspend fun getAll(): List<Event>
    suspend fun getById(id: EventId): Event?
    suspend fun getByName(name: EventName): Event?
    suspend fun getAllByGroupId(groupId: EventGroupId): List<Event>
    suspend fun insert(
        newEvent: NewEvent,
        riotTournamentId: RiotTournamentId,
    ): Event?
    suspend fun update(
        event: Event,
        update: EventUpdate,
    ): Event?
}
