package com.lowbudgetlcs.domain.event.core.port

import com.lowbudgetlcs.domain.event.core.model.Event
import com.lowbudgetlcs.domain.event.core.model.EventQuery
import com.lowbudgetlcs.domain.event.core.model.EventUpdate
import com.lowbudgetlcs.domain.event.core.model.EventWithSeries
import com.lowbudgetlcs.domain.event.core.model.EventWithTeams
import com.lowbudgetlcs.domain.event.core.model.NewEvent
import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.series.core.model.SeriesQuery
import com.lowbudgetlcs.domain.team.core.model.types.TeamId

interface IEventService {
    /**
     * Fetch an event by id.
     *
     * @param EventId the id of the event.
     * @return the specified event.
     *
     * @throws NoSuchElementException when the event is not found.
     * @throws com.lowbudgetlcs.domain.RepositoryException when the underlying repository fails.
     */
    suspend fun getEvent(id: EventId): Event

    /**
     *  Fetches all events.
     *
     * @return a list containing all events.
     */
    suspend fun getAllEvents(query: EventQuery? = null): List<Event>


    /**
     * Create an event from a NewEvent and NewTournament.
     *
     * @param NewEvent event details.
     * @return the newly created event.
     *
     * @throws IllegalArgumentException if the event cannot be created.
     * @throws com.lowbudgetlcs.domain.RepositoryException if the underlying repositories fail.
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
     * @throws com.lowbudgetlcs.domain.RepositoryException when the underlying repositories
     * fail.
     */
    suspend fun patchEvent(
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
    suspend fun getEventWithTeams(id: EventId): EventWithTeams

    /**
     * Fetches all events and includes series that are registered to the event
     *
     * @param EventId the event to fetch.
     * @return the specified event with all child series.
     *
     * @throws NoSuchElementException if the specified event cannot be found
     */
    suspend fun getEventWithSeries(
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
    suspend fun addTeam(
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
    suspend fun removeTeam(
        eventId: EventId,
        teamId: TeamId,
    ): EventWithTeams
}
