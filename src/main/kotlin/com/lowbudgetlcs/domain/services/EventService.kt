package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.RepositoryException
import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.domain.models.tournament.NewTournament
import com.lowbudgetlcs.gateways.ITournamentGateway
import com.lowbudgetlcs.repositories.IEventGroupRepository
import com.lowbudgetlcs.repositories.IEventRepository
import java.time.Instant

class EventService(
    private val eventRepo: IEventRepository,
    private val eventGroupRepo: IEventGroupRepository,
    private val tournamentGateway: ITournamentGateway
) {
    fun getAllEvents(): List<Event> = eventRepo.getAll()

    fun getEvent(id: EventId): Event =
        eventRepo.getById(id) ?: throw NoSuchElementException("Player with ${id.value} not found.")

    fun createEvent(event: NewEvent, tournament: NewTournament): Event {
        if (event.name.isBlank()) throw IllegalArgumentException("Event name cannot be blank.")
        if (isNameTaken(event.name)) throw IllegalArgumentException("Event '${event.name}' already exists.")
        if (!event.startDate.isBefore(event.endDate)) throw IllegalArgumentException("Events cannot start after they end.")
        val t = tournamentGateway.create(tournament)
            ?: throw RepositoryException("Failed to register tournament with Riot Games.")
        return eventRepo.insert(event, t.id) ?: throw RepositoryException("Failed to create event.")
    }

    fun patchEvent(id: EventId, update: EventUpdate): Event {
        val event = getEvent(id)
        update.name?.let { if (isNameTaken(it)) throw IllegalArgumentException("Event '${event.name}' already exists.") }
        val start = update.startDate ?: event.startDate
        val end = update.endDate ?: event.endDate
        if (end.isBefore(start)) throw IllegalArgumentException("Events cannot start before they end.")
        return eventRepo.update(event.patch(update)) ?: throw RepositoryException("Failed to update event.")
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

    private fun isNameTaken(name: String): Boolean = eventRepo.getAll().any { it.name == name }
}