package com.lowbudgetlcs.repositories.event

import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupId
import com.lowbudgetlcs.domain.models.events.Event
import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.events.EventUpdate
import com.lowbudgetlcs.domain.models.events.NewEvent
import com.lowbudgetlcs.domain.models.events.RiotTournamentId

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
