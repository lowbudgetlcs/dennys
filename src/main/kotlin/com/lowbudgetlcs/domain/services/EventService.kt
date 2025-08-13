package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.domain.models.tournament.NewTournament
import com.lowbudgetlcs.gateways.ITournamentGateway
import com.lowbudgetlcs.repositories.IEventGroupRepository
import com.lowbudgetlcs.repositories.IEventRepository

class EventService(
    private val eventRepo: IEventRepository,
    private val eventGroupRepo: IEventGroupRepository,
    private val tournamentGateway: ITournamentGateway
) {
    fun getAllEvents(): List<Event> = eventRepo.getAll()

    fun createEvent(event: NewEvent, tournament: NewTournament): Event? = tournamentGateway.create(tournament)?.let {
        eventRepo.insert(event, it.id)
    }

    fun createEventGroup(group: NewEventGroup): EventGroup? = eventGroupRepo.insert(group)

    fun getEventsWithGroups(): List<EventWithGroup> {
        val events = eventRepo.getAll()
        val groups = eventGroupRepo.getAll()
        return events.mapNotNull { e ->
            val group = groups.firstOrNull { g -> e.eventGroupId == g.id }
            group?.let { e.toEventWithGroup(it) }
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