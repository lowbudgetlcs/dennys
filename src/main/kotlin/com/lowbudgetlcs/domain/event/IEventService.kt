package com.lowbudgetlcs.domain.event

import com.lowbudgetlcs.domain.event.models.Event
import com.lowbudgetlcs.domain.event.models.EventQuery
import com.lowbudgetlcs.domain.event.models.EventUpdate
import com.lowbudgetlcs.domain.event.models.EventWithSeries
import com.lowbudgetlcs.domain.event.models.EventWithTeams
import com.lowbudgetlcs.domain.event.models.NewEvent
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.models.SeriesQuery
import com.lowbudgetlcs.domain.models.team.TeamId
import com.lowbudgetlcs.repositories.DatabaseException

interface IEventService {
    /**
     *  Fetches all events.
     *
     * @return a list containing all events.
     */
    fun getAllEvents(query: EventQuery? = null): List<Event>

    /**
     * Fetch an event by id.
     *
     * @param EventId the id of the event.
     * @return the specified event.
     *
     * @throws NoSuchElementException when the event is not found.
     * @throws DatabaseException when the underlying repository fails.
     */
    fun getEvent(id: EventId): Event

    /**
     * Create an event from a NewEvent and NewTournament.
     *
     * @param NewEvent event details.
     * @return the newly created event.
     *
     * @throws IllegalArgumentException if the event cannot be created.
     * @throws DatabaseException if the underlying repositories fail.
     */
    suspend fun createEvent(event: NewEvent): Event

    /**
     * Updates event details.
     *
     * @param Event the event to update.
     * @param EventUpdate the new event information.
     * @return the updated event.
     *
     * @throws IllegalArgumentException if the new details are invalid
     * @throws DatabaseException when the underlying repositories
     * fail.
     */
    fun patchEvent(
        id: EventId,
        update: EventUpdate,
    ): Event

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
     * Fetches all events and includes series that are registered to the event
     *
     * @param EventId the event to fetch.
     * @return the specified event with all child series.
     *
     * @throws NoSuchElementException if the specified event cannot be found
     */
    fun getEventWithSeries(
        id: EventId,
        query: SeriesQuery? = null,
    ): EventWithSeries

    /**
     * Associate a team with an event
     *
     * @param EventId the target event.
     * @param TeamId the team to add.
     * @return the event with all registered teams.
     *
     * @throws NoSuchElementException if the specified event or team doesn't exist
     */
    fun addTeam(
        eventId: EventId,
        teamId: TeamId,
    ): EventWithTeams

    /**
     * Unassociate a team with an event
     *
     * @param EventId the target event.
     * @param TeamId the team to add.
     * @return the event with all registered teams.
     *
     * @throws NoSuchElementException if the specified event or team doesn't exist
     */
    fun removeTeam(
        eventId: EventId,
        teamId: TeamId,
    ): EventWithTeams
}
