package com.lowbudgetlcs.domain.services.event

import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.domain.models.team.TeamId
import com.lowbudgetlcs.gateways.GatewayException
import com.lowbudgetlcs.gateways.riot.tournament.IRiotTournamentGateway
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.event.IEventRepository
import com.lowbudgetlcs.repositories.series.ISeriesRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EventService(
    private val eventRepo: IEventRepository,
    private val tournamentGateway: IRiotTournamentGateway,
    private val teamRepo: ITeamRepository,
    private val seriesRepo: ISeriesRepository,
) : IEventService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun getAllEvents(): List<Event> {
        logger.debug("Fetching all events...")
        return eventRepo.getAll()
    }

    override fun getEvent(id: EventId): Event {
        logger.debug("Getting event by '$id'...")
        return eventRepo.getById(id) ?: throw NoSuchElementException("Event with id '${id.value}' not found.")
    }

    override fun getEventWithTeams(id: EventId): EventWithTeams {
        logger.debug("Getting event by '$id' (with teams)...")
        val event = getEvent(id)
        val teams = teamRepo.getAll().filter { it.eventId == id }
        return event.toEventWithTeams(teams)
    }

    override fun getEventWithSeries(id: EventId): EventWithSeries {
        logger.debug("Getting event by '$id' (with series)...")
        val event = getEvent(id)
        val series = seriesRepo.getAllByEventId(id).filter { it.eventId == id }
        return event.toEventWithSeries(series)
    }

    override suspend fun createEvent(event: NewEvent): Event {
        logger.debug("Creating new event...")
        logger.debug(event.toString())
        if (event.name.isBlank()) throw IllegalArgumentException("Event name cannot be blank.")
        if (!event.startDate.isBefore(
                event.endDate,
            )
        ) {
            throw IllegalArgumentException("Events cannot start after they end.")
        }
        val t =
            tournamentGateway.create(event.name)
                ?: throw GatewayException("Failed to register tournament with Riot Games.")
        isNameTaken(event.name)
        return eventRepo.insert(event, t.id) ?: throw DatabaseException("Failed to create event.")
    }

    override fun patchEvent(
        id: EventId,
        update: EventUpdate,
    ): Event {
        logger.debug("Patching event '$id'...")
        logger.debug(update.toString())
        val event = getEvent(id)
        update.name?.let {
            isNameTaken(it)
        }
        val start = update.startDate ?: event.startDate
        val end = update.endDate ?: event.endDate
        if (end.isBefore(start)) throw IllegalArgumentException("Events cannot start before they end.")
        return eventRepo.update(event.patch(update)) ?: throw DatabaseException("Failed to update event.")
    }

    override fun addTeam(
        eventId: EventId,
        teamId: TeamId,
    ): EventWithTeams {
        logger.debug("Adding team '$teamId' to event '$eventId'...")
        doesEventExist(eventId)
        doesTeamExist(teamId)
        teamRepo.updateEventId(teamId, eventId) ?: throw DatabaseException("Failed to add team to event.")
        return getEventWithTeams(eventId)
    }

    override fun removeTeam(
        eventId: EventId,
        teamId: TeamId,
    ): EventWithTeams {
        logger.debug("Removing team '$teamId' from event '$eventId'...")
        doesEventExist(eventId)
        doesTeamExist(teamId)
        teamRepo.updateEventId(teamId, null) ?: throw DatabaseException("Failed to remove team from event.")
        return getEventWithTeams(eventId)
    }

    /**
     * Checks if an event name is taken.
     * @return false if name is not taken.
     * @throws IllegalArgumentException when name already exists.
     */
    private fun isNameTaken(name: String): Boolean {
        logger.debug("Checking if '$name' is available...")
        return if (eventRepo.getByName(name) != null) {
            throw IllegalArgumentException("Event '$name' already exists.")
        } else {
            false
        }
    }

    /**
     * Checks if an event exists.
     * @return true if event exists.
     * @throws NoSuchElementException if event does not exist.
     */
    private fun doesEventExist(eventId: EventId): Boolean {
        logger.debug("Checking if event '$eventId' exists...")
        return if (eventRepo.getById(eventId) ==
            null
        ) {
            throw NoSuchElementException("Event with id '${eventId.value}' not found.")
        } else {
            true
        }
    }

    /**
     * Checks if team exists
     * @return true if team exists.
     * @throws NoSuchElementException if team does not exist.
     */
    private fun doesTeamExist(teamId: TeamId): Boolean {
        logger.debug("Checking if team '$teamId' exists...")
        return if (teamRepo.getById(teamId) ==
            null
        ) {
            throw NoSuchElementException("Team with id '${teamId.value}' not found.")
        } else {
            true
        }
    }
}
