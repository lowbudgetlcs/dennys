package com.lowbudgetlcs.repositories.inmemory

import com.lowbudgetlcs.domain.models.event.Event
import com.lowbudgetlcs.domain.models.event.NewEvent
import com.lowbudgetlcs.domain.tournament.Tournament
import com.lowbudgetlcs.repositories.IEventRepository
import java.time.Instant.now

class InMemoryEventRepository : IEventRepository {
    private val events: MutableList<Event> = mutableListOf()
    override fun getById(id: Int): Event? = events.getOrNull(id)

    override fun insert(
        event: NewEvent,
        tournament: Tournament
    ): Event? {
        val id = events.size
        val e = Event(
            id = id,
            name = event.name,
            description = event.description,
            riotTournamentId = tournament.id,
            createdAt = now(),
            startDate = event.startDate,
            endDate = event.endDate,
            status = event.status
        )
        events.add(id, e)
        return e
    }
}