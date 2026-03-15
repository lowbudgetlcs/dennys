package com.lowbudgetlcs.domain.event

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.event.models.*
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventName
import com.lowbudgetlcs.domain.series.models.SeriesQuery
import com.lowbudgetlcs.domain.series.models.filterByParticipants
import com.lowbudgetlcs.domain.series.models.filterByStage
import com.lowbudgetlcs.domain.team.models.TeamUpdate
import com.lowbudgetlcs.domain.team.models.types.TeamId
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

    override fun getAllEvents(query: EventQuery?): List<Event> {
        logger.debug("Fetching all events...")
        query?.run { logger.debug("(Query: '$query')") }
        return eventRepo.getAll().filterByName(query).filterByStatus(query)
    }

    override fun getEvent(id: EventId): Event {
        logger.debug("Getting event by '$id'...")
        return eventRepo.getById(id) ?: throw NoSuchElementException("Event with id '${id.value}' not found.")
    }

    override fun getEventWithTeams(id: EventId): EventWithTeams {
        logger.debug("Getting event by '$id' (with teams)...")
        val event = getEvent(id)
        val teams = teamRepo.getByEventId(id)
        return event.toEventWithTeams(teams)
    }

    override fun getEventWithSeries(
        id: EventId,
        query: SeriesQuery?,
    ): EventWithSeries {
        logger.debug("Getting event by '$id' (with series)...")
        query?.run { logger.debug("(Query: '$query')") }
        val event = getEvent(id)
        val series = seriesRepo.getAllByEventId(id).filterByStage(query).filterByParticipants(query)
        return event.toEventWithSeries(series)
    }

    override suspend fun createEvent(event: NewEvent): Event {
        logger.debug("Creating new event...")
        logger.debug(event.toString())
        require(event.startDate.isBefore(event.endDate)) { "Event start date must be before end date." }
        check(!isNameTaken(event.name)) { "Event '${event.name}' already exists." }
        val t =
            tournamentGateway.create(event.name)
                ?: throw GatewayException("Failed to register tournament with Riot Games.")
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
            check(!isNameTaken(it)) { "Event with name '$it' already exists." }
        }
        val start = update.startDate ?: event.startDate
        val end = update.endDate ?: event.endDate
        check(start.isBefore(end)) { "Event start date must be before end date." }
        return eventRepo.update(event, update)
            ?: throw DatabaseException("Failed to update event with id '${id.value}'.")
    }

    private fun validateTeam(eventId: EventId, teamId: TeamId) {
        if (!doesEventExist(eventId)) throw NoSuchElementException("Event with id '${eventId.value}' not found.")
        if (!doesTeamExist(teamId)) throw NoSuchElementException("Team with id '${teamId.value}' not found.")
    }

    override fun addTeam(
        eventId: EventId,
        teamId: TeamId,
    ): EventWithTeams {
        logger.debug("Adding team '$teamId' to event '$eventId'...")
        validateTeam(eventId, teamId)
        val team = teamRepo.getById(teamId) ?: throw NoSuchElementException("Team with id '${teamId.value}' not found.")
        val teamPatch = TeamUpdate(eventId = PatchField.Value(eventId))
        teamRepo.update(team, teamPatch) ?: throw DatabaseException("Failed to add team to event.")
        return getEventWithTeams(eventId)
    }

    override fun removeTeam(
        eventId: EventId,
        teamId: TeamId,
    ): EventWithTeams {
        logger.debug("Removing team '$teamId' from event '$eventId'...")
        validateTeam(eventId, teamId)
        val team = teamRepo.getById(teamId) ?: throw NoSuchElementException("Team with id '${teamId.value}' not found.")
        val teamPatch = TeamUpdate(eventId = PatchField.Value(null))
        teamRepo.update(team, teamPatch) ?: throw DatabaseException("Failed to add team to event.")
        return getEventWithTeams(eventId)
    }

    /**
     * Checks if an event name is taken.
     * @return true if name is taken, false otherwise.
     */
    fun isNameTaken(name: EventName): Boolean {
        logger.debug("Checking if '$name' is available...")
        return eventRepo.getByName(name) != null
    }

    /**
     * Checks if an event exists.
     * @return true if event exists, false otherwise.
     */
    fun doesEventExist(eventId: EventId): Boolean {
        logger.debug("Checking if event '$eventId' exists...")
        return eventRepo.getById(eventId) != null
    }

    /**
     * Checks if team exists
     * @return true if team exists, false otherwise
     */
    fun doesTeamExist(teamId: TeamId): Boolean {
        logger.debug("Checking if team '$teamId' exists...")
        return teamRepo.getById(teamId) != null
    }
}
