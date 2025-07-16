package com.lowbudgetlcs.repositories.inmemory

import com.lowbudgetlcs.domain.models.tournament.TournamentId
import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.repositories.IEventRepository
import java.time.Instant.now

class InMemoryEventRepository : IEventRepository {
    private val events: MutableList<Event> = mutableListOf()
    fun clear() {
        events.clear()
    }

    override fun getAll(): List<Event> = events

    override fun getAllByGroupId(groupId: EventGroupId): List<Event> = events.filter { event ->
        event.eventGroupId == groupId
    }

    override fun getById(id: EventId): Event? = events.getOrNull(id.value)

    override fun insert(
        newEvent: NewEvent, tournamentId: TournamentId
    ): Event? {
        val id = events.size.toEventId()
        val e = newEvent.toEvent(id, now(), tournamentId)
        events.add(id.value, e)
        return e
    }
}