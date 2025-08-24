package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.events.*
import com.lowbudgetlcs.domain.models.team.TeamId
import com.lowbudgetlcs.domain.models.tournament.NewTournament

interface IEventService {
    /**
     * Fetches all events.
     */
    fun getAllEvents(): List<Event>

    /**
     * Fetch an event by id.
     *
     * @param EventId the id of the event.
     * @return the specified event.
     *
     * @throws NoSuchElementException when the event is not found.
     * @throws com.lowbudgetlcs.repositories.DatabaseException when the underlying repository fails.
     */
    fun getEvent(id: EventId): Event

    /**
     * Create an event from a NewEvent and NewTournament.
     *
     * @param NewEvent event details.
     * @param NewTournament the riot tournament to include in the event.
     * @return the newly created event.
     *
     * @throws IllegalArgumentException if the event cannot be created.
     * @throws com.lowbudgetlcs.repositories.DatabaseException if the underlying repositories fail.
     */
    fun createEvent(event: NewEvent, tournament: NewTournament): Event

    /**
     * Updates event details.
     *
     * @param Event the event to update.
     * @param EventUpdate the new event information.
     * @return the updated event.
     *
     * @throws IllegalArgumentException if the new details are invalid
     * @throws com.lowbudgetlcs.repositories.DatabaseException when the underlying repositories fail.
     */
    fun patchEvent(id: EventId, update: EventUpdate): Event

    /**
     * Fetches all events and includes teams that are registered to the event
     *
     * @param EventId the event to fetch.
     * @return the specified event with all child teams.
     *
     * @throws NoSuchElementException if the specified event cannot be found
     */
    fun getEventWithTeams(id: EventId): EventWithTeams

    /**
     * Associate a team with an event
     *
     * @param EventId the target event.
     * @param TeamId the team to add.
     * @return the event with all registered teams.
     *
     * @throws NoSuchElementException if the specified event or team doesn't exist
     */
    fun addTeam(eventId: EventId, teamId: TeamId): EventWithTeams

    /**
     * Unassociate a team with an event
     *
     * @param EventId the target event.
     * @param TeamId the team to add.
     * @return the event with all registered teams.
     *
     * @throws NoSuchElementException if the specified event or team doesn't exist
     */
    fun removeTeam(eventId: EventId, teamId: TeamId): EventWithTeams
}