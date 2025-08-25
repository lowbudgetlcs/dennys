package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.gateways.GatewayException
import com.lowbudgetlcs.gateways.IRiotTournamentGateway
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.IEventRepository

class EventService(
    private val eventRepo: IEventRepository, private val tournamentGateway: IRiotTournamentGateway
) : IEventService {
    override fun getAllEvents(): List<Event> = eventRepo.getAll()

    override fun getEventWithTeams(id: EventId): List<EventWithTeams> = TODO("Not yet implemented")

    override fun getEvent(id: EventId): Event =
        eventRepo.getById(id) ?: throw NoSuchElementException("Event with ${id.value} not found.")

    override suspend fun createEvent(event: NewEvent): Event {
        if (event.name.isBlank()) throw IllegalArgumentException("Event name cannot be blank.")
        if (isNameTaken(event.name)) throw IllegalArgumentException("Event '${event.name}' already exists.")
        if (!event.startDate.isBefore(event.endDate)) throw IllegalArgumentException("Events cannot start after they end.")
        val t = tournamentGateway.create(event.name)
            ?: throw GatewayException("Failed to register tournament with Riot Games.")
        return eventRepo.insert(event, t.id) ?: throw DatabaseException("Failed to create event.")
    }

    override fun patchEvent(id: EventId, update: EventUpdate): Event {
        val event = getEvent(id)
        update.name?.let { if (isNameTaken(it)) throw IllegalArgumentException("Event '${update.name}' already exists.") }
        val start = update.startDate ?: event.startDate
        val end = update.endDate ?: event.endDate
        if (end.isBefore(start)) throw IllegalArgumentException("Events cannot start before they end.")
        return eventRepo.update(event.patch(update)) ?: throw DatabaseException("Failed to update event.")
    }

    private fun isNameTaken(name: String): Boolean = eventRepo.getAll().any { it.name == name }
}