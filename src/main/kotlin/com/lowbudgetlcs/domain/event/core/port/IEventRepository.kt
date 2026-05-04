package com.lowbudgetlcs.domain.event.core.port

import com.lowbudgetlcs.domain.event.core.model.Event
import com.lowbudgetlcs.domain.event.core.model.EventUpdate
import com.lowbudgetlcs.domain.event.core.model.NewEvent
import com.lowbudgetlcs.domain.event.core.model.RiotTournamentId
import com.lowbudgetlcs.domain.event.core.model.types.EventGroupId
import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.event.core.model.types.EventName

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
