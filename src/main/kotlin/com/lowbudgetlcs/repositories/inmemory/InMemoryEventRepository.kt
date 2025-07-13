package com.lowbudgetlcs.repositories.inmemory

import com.lowbudgetlcs.domain.models.Event
import com.lowbudgetlcs.domain.models.EventId
import com.lowbudgetlcs.domain.models.EventStatus
import com.lowbudgetlcs.domain.models.NewEvent
import com.lowbudgetlcs.domain.models.TournamentId
import com.lowbudgetlcs.repositories.IEventRepository
import java.time.Instant.now

class InMemoryEventRepository : IEventRepository {
    private val events: MutableList<Event> = mutableListOf()
    fun clear() {
        events.clear()
    }
    override fun getById(id: EventId): Event? = events.getOrNull(id.value)

    override fun insert(
        event: NewEvent,
        tournamentId: TournamentId
    ): Event? {
        val id = EventId(events.size)
        val e = Event(
            id = id,
            name = event.name,
            description = event.description,
            tournamentId = tournamentId,
            createdAt = now(),
            startDate = event.startDate,
            endDate = event.endDate,
            status = event.status
        )
        events.add(id.value, e)
        return e
    }

    override fun updateStatusById(
        id: EventId,
        status: EventStatus
    ): Event? {
        try {
            events[id.value] = events[id.value].copy(status = status)
            return events[id.value]
        } catch (e: IndexOutOfBoundsException) {
            return null
        }
    }
}