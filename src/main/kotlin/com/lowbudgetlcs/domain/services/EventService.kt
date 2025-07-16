package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.NewTournament
import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.repositories.IEventGroupRepository
import com.lowbudgetlcs.repositories.IEventRepository
import com.lowbudgetlcs.repositories.ITournamentRepository

class EventService(
    private val eventRepo: IEventRepository,
    private val eventGroupRepo: IEventGroupRepository,
    private val tournamentRepo: ITournamentRepository
) {
    fun create(event: NewEvent, tournament: NewTournament): Event? = tournamentRepo.create(tournament)?.let {
        eventRepo.insert(event, it.id)
    }

    fun getEvents(): List<EventWithGroup> {
        val events = eventRepo.getAll()
        val groups = eventGroupRepo.getAll()
        return events.map { e ->
            val group = groups.first { g ->
                e.eventGroupId == g.id
            }
            e.toEventWithGroup(group)
        }
    }

    fun getEventsByGroupId(group: EventGroupId): List<EventWithGroup> {
        val events = eventRepo.getAllByGroupId(group)
        val group = eventGroupRepo.getById(group)
        return events.map { it.toEventWithGroup(group) }
    }

    fun getEventGroups(): List<EventGroup> = eventGroupRepo.getAll()

    fun getEventGroupById(id: EventGroupId): EventGroup? = eventGroupRepo.getById(id)

    fun getEvent(id: EventId): Event? = eventRepo.getById(id)
}