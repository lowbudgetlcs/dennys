package com.lowbudgetlcs.domain.event.core

import com.lowbudgetlcs.domain.RepositoryException
import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.event.core.model.Event
import com.lowbudgetlcs.domain.event.core.model.EventQuery
import com.lowbudgetlcs.domain.event.core.model.EventUpdate
import com.lowbudgetlcs.domain.event.core.model.EventWithSeries
import com.lowbudgetlcs.domain.event.core.model.EventWithTeams
import com.lowbudgetlcs.domain.event.core.model.NewEvent
import com.lowbudgetlcs.domain.event.core.model.filterByName
import com.lowbudgetlcs.domain.event.core.model.filterByStatus
import com.lowbudgetlcs.domain.event.core.model.toEventWithSeries
import com.lowbudgetlcs.domain.event.core.model.toEventWithTeams
import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.event.core.model.types.EventName
import com.lowbudgetlcs.domain.event.core.port.IEventRepository
import com.lowbudgetlcs.domain.event.core.port.IEventService
import com.lowbudgetlcs.domain.series.core.model.SeriesQuery
import com.lowbudgetlcs.domain.series.core.port.ISeriesService
import com.lowbudgetlcs.domain.team.core.model.TeamUpdate
import com.lowbudgetlcs.domain.team.core.model.types.TeamId
import com.lowbudgetlcs.domain.team.core.port.ITeamRepository
import com.lowbudgetlcs.gateways.GatewayException
import com.lowbudgetlcs.gateways.riot.tournament.IRiotTournamentGateway
import com.lowbudgetlcs.logger

class EventService(
    private val eventRepo: IEventRepository,
    private val tournamentGateway: IRiotTournamentGateway,
    private val teamRepo: ITeamRepository,
    private val seriesService: ISeriesService,
) : IEventService {


    override suspend fun getEvent(id: EventId): Event {
        logger.debug("Getting event by '$id'...")
        return eventRepo.getById(id) ?: throw NoSuchElementException("Event with id '${id.value}' not found.")
    }

    override suspend fun getEventWithTeams(id: EventId): EventWithTeams {
        logger.debug("Getting event by '$id' (with teams)...")
        val event = getEvent(id)
        val teams = teamRepo.getByEventId(id)
        return event.toEventWithTeams(teams)
    }

    override suspend fun getEventWithSeries(
        id: EventId,
        query: SeriesQuery?,
    ): EventWithSeries {
        logger.debug("Getting event by '$id' (with series)...")
        query?.run { logger.debug("(Query: '$query')") }
        val event = getEvent(id)
        val series = seriesService.getAllSeriesFromEvent(id)
        return event.toEventWithSeries(series)
    }

    override suspend fun getAllEvents(query: EventQuery?): List<Event> {
        logger.debug("Fetching all events...")
        query?.run { logger.debug("(Query: '{}')", query) }
        return eventRepo.getAll().filterByName(query).filterByStatus(query)
    }

    override suspend fun createEvent(event: NewEvent): Event {
        logger.debug("Creating new event...")
        logger.debug(event.toString())
        require(event.startDate.isBefore(event.endDate)) { "Event start date must be before end date." }
        check(!isNameTaken(event.name)) { "Event '${event.name}' already exists." }
        val t =
            tournamentGateway.create(event.name)
                ?: throw GatewayException("Failed to register tournament with Riot Games.")
        return eventRepo.insert(event, t.id) ?: throw RepositoryException("Failed to create event.")
    }

    override suspend fun patchEvent(
        id: EventId,
        update: EventUpdate,
    ): Event {
        logger.debug("Patching event '$id'...")
        logger.debug(update.toString())
        val event = getEvent(id)
        update.name?.let {
            check(!isNameTaken(it)) { "Event with name '$it' already exists." }
        }
        val start = update.startDate ?: event.startDate
        val end = update.endDate ?: event.endDate
        check(start.isBefore(end)) { "Event start date must be before end date." }
        return eventRepo.update(event, update)
            ?: throw RepositoryException("Failed to update event with id '${id.value}'.")
    }

    private suspend fun validateTeam(
        eventId: EventId,
        teamId: TeamId,
    ) {
        if (!doesEventExist(eventId)) throw NoSuchElementException("Event with id '${eventId.value}' not found.")
        if (!doesTeamExist(teamId)) throw NoSuchElementException("Team with id '${teamId.value}' not found.")
    }

    override suspend fun addTeam(
        eventId: EventId,
        teamId: TeamId,
    ): EventWithTeams {
        logger.debug("Adding team '$teamId' to event '$eventId'...")
        validateTeam(eventId, teamId)
        val team = teamRepo.getById(teamId) ?: throw NoSuchElementException("Team with id '${teamId.value}' not found.")
        val teamPatch = TeamUpdate(eventId = PatchField.Value(eventId))
        teamRepo.update(team, teamPatch) ?: throw RepositoryException("Failed to add team to event.")
        return getEventWithTeams(eventId)
    }

    override suspend fun removeTeam(
        eventId: EventId,
        teamId: TeamId,
    ): EventWithTeams {
        logger.debug("Removing team '$teamId' from event '$eventId'...")
        validateTeam(eventId, teamId)
        val team = teamRepo.getById(teamId) ?: throw NoSuchElementException("Team with id '${teamId.value}' not found.")
        val teamPatch = TeamUpdate(eventId = PatchField.Value(null))
        teamRepo.update(team, teamPatch) ?: throw RepositoryException("Failed to add team to event.")
        return getEventWithTeams(eventId)
    }

    /**
     * Checks if an event name is taken.
     * @return true if name is taken, false otherwise.
     */
    suspend fun isNameTaken(name: EventName): Boolean {
        logger.debug("Checking if '$name' is available...")
        return eventRepo.getByName(name) != null
    }

    /**
     * Checks if an event exists.
     * @return true if event exists, false otherwise.
     */
    suspend fun doesEventExist(eventId: EventId): Boolean {
        logger.debug("Checking if event '$eventId' exists...")
        return eventRepo.getById(eventId) != null
    }

    /**
     * Checks if team exists
     * @return true if team exists, false otherwise
     */
    suspend fun doesTeamExist(teamId: TeamId): Boolean {
        logger.debug("Checking if team '$teamId' exists...")
        return teamRepo.getById(teamId) != null
    }
}
