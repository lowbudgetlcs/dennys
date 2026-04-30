package com.lowbudgetlcs.domain.event.core.port

import com.lowbudgetlcs.domain.event.core.model.Event
import com.lowbudgetlcs.domain.event.core.model.EventUpdate
import com.lowbudgetlcs.domain.event.core.model.NewEvent
import com.lowbudgetlcs.domain.event.core.model.RiotTournamentId
import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.event.core.model.types.EventName
import com.lowbudgetlcs.domain.event.core.model.types.EventGroupId

interface IEventRepository {
    fun getAll(): List<Event>

    fun getById(id: EventId): Event?

    fun getByName(name: EventName): Event?

    fun getAllByGroupId(groupId: EventGroupId): List<Event>

    fun insert(
        newEvent: NewEvent,
        riotTournamentId: RiotTournamentId,
    ): Event?

    fun update(
        event: Event,
        update: EventUpdate,
    ): Event?
}
