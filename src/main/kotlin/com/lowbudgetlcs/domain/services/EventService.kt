package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.RepositoryException
import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.domain.models.tournament.NewTournament
import com.lowbudgetlcs.gateways.ITournamentGateway
import com.lowbudgetlcs.repositories.IEventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class EventService(
    private val eventRepo: IEventRepository, private val tournamentGateway: ITournamentGateway
) : IEventService {
    override fun getAllEvents(): List<Event> = eventRepo.getAll()

    override fun getEventWithTeams(id: EventId): List<EventWithTeams> = TODO("Not yet implemented")

    override fun getEvent(id: EventId): Event =
        eventRepo.getById(id) ?: throw NoSuchElementException("Event with ${id.value} not found.")

    override fun createEvent(event: NewEvent, tournament: NewTournament): Event {
        if (event.name.isBlank()) throw IllegalArgumentException("Event name cannot be blank.")
        if (isNameTaken(event.name)) throw IllegalArgumentException("Event '${event.name}' already exists.")
        if (!event.startDate.isBefore(event.endDate)) throw IllegalArgumentException("Events cannot start after they end.")
        // TODO: Services may need to be refactored to support suspend functions.
        val t = tournamentGateway.create(tournament)
            ?: throw RepositoryException("Failed to register tournament with Riot Games.")
        return eventRepo.insert(event, t.id) ?: throw RepositoryException("Failed to create event.")
    }

    override fun patchEvent(id: EventId, update: EventUpdate): Event {
        val event = getEvent(id)
        update.name?.let { if (isNameTaken(it)) throw IllegalArgumentException("Event '${update.name}' already exists.") }
        val start = update.startDate ?: event.startDate
        val end = update.endDate ?: event.endDate
        if (end.isBefore(start)) throw IllegalArgumentException("Events cannot start before they end.")
        return eventRepo.update(event.patch(update)) ?: throw RepositoryException("Failed to update event.")
    }

    private fun isNameTaken(name: String): Boolean = eventRepo.getAll().any { it.name == name }
}