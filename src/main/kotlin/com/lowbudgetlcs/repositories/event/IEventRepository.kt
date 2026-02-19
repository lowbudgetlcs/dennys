package com.lowbudgetlcs.repositories.event

import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.EventUpdate
import com.lowbudgetlcs.domain.event.models.NewEvent
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.RiotTournamentId
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupId

interface IEventRepository {
    fun getAll(): List<Event>

    fun getById(id: EventId): Event?

    fun getByName(name: String): Event?

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
