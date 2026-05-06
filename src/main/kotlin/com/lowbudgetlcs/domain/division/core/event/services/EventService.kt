package com.lowbudgetlcs.domain.division.core.event.services

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.RepositoryException
import com.lowbudgetlcs.domain.division.core.event.model.Event
import com.lowbudgetlcs.domain.division.core.event.model.EventQuery
import com.lowbudgetlcs.domain.division.core.event.model.EventUpdate
import com.lowbudgetlcs.domain.division.core.event.model.EventWithTeams
import com.lowbudgetlcs.domain.division.core.event.model.NewEvent
import com.lowbudgetlcs.domain.division.core.event.model.filterByName
import com.lowbudgetlcs.domain.division.core.event.model.filterByStatus
import com.lowbudgetlcs.domain.division.core.event.model.toEventWithTeams
import com.lowbudgetlcs.domain.division.core.event.model.types.EventId
import com.lowbudgetlcs.domain.division.core.event.model.types.EventName
import com.lowbudgetlcs.domain.division.core.event.port.IEventRepository
import com.lowbudgetlcs.domain.division.core.series.services.SeriesService
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
    private val seriesService: SeriesService,
) {

    /**
     * Fetch an event by id.
     *
     * @param EventId the id of the event.
     * @return the specified event.
     *
     * @throws NoSuchElementException when the event is not found.
     * @throws RepositoryException when the underlying repository fails.
     */
    suspend fun getEvent(id: EventId): Event {
        logger.debug("Getting event by '$id'...")
        return eventRepo.getById(id) ?: throw NoSuchElementException("Event with id '${id.value}' not found.")
    }

    /**
     * Fetches all events and includes teams that are registered to the event
     *
     * @param EventId the event to fetch.
     * @return the specified event with all child teams.
     *
     * @throws NoSuchElementException if the specified event cannot be found
     */
    suspend fun getEventWithTeams(id: EventId): EventWithTeams {
        logger.debug("Getting event by '$id' (with teams)...")
        val event = getEvent(id)
        val teams = teamRepo.getByEventId(id)
        return event.toEventWithTeams(teams)
    }


    /**
     *  Fetches all events.
     *
     * @return a list containing all events.
     */
    suspend fun getAllEvents(query: EventQuery?): List<Event> {
        logger.debug("Fetching all events...")
        query?.run { logger.debug("(Query: '{}')", query) }
        return eventRepo.getAll().filterByName(query).filterByStatus(query)
    }

    /**
     * Create an event from a NewEvent and NewTournament.
     *
     * @param NewEvent event details.
     * @return the newly created event.
     *
     * @throws IllegalArgumentException if the event cannot be created.
     * @throws RepositoryException if the underlying repositories fail.
     */
    suspend fun createEvent(event: NewEvent): Event {
        logger.debug("Creating new event...")
        logger.debug(event.toString())
        require(event.startDate.isBefore(event.endDate)) { "Event start date must be before end date." }
        check(!isNameTaken(event.name)) { "Event '${event.name}' already exists." }
        val t = tournamentGateway.create(event.name)
            ?: throw GatewayException("Failed to register tournament with Riot Games.")
        return eventRepo.insert(event, t.id) ?: throw RepositoryException("Failed to create event.")
    }

    /**
     * Updates event details.
     *
     * @param Event the event to update.
     * @param EventUpdate the new event information.
     * @return the updated event.
     *
     * @throws IllegalArgumentException if the new details are invalid
     * @throws RepositoryException when the underlying repositories
     * fail.
     */
    suspend fun patchEvent(
        id: EventId,
        update: EventUpdate,
    ): Event {
        logger.debug("Patching event '$id'...")
        logger.debug(update.toString())
        val event = eventRepo.getById(id) ?: throw NoSuchElementException("Event with id '${id.value}' not found.")
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

    /**
     * Associate a team with an event
     *
     * @param EventId the target event.
     * @param TeamId the team to add.
     * @return the event with all registered teams.
     *
     * @throws NoSuchElementException if the specified event or team doesn't exist
     */
    suspend fun addTeam(
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

    /**
     * Unassociate a team with an event
     *
     * @param EventId the target event.
     * @param TeamId the team to add.
     * @return the event with all registered teams.
     *
     * @throws NoSuchElementException if the specified event or team doesn't exist
     */
    suspend fun removeTeam(
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
