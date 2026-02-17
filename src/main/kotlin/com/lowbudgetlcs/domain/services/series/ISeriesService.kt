package com.lowbudgetlcs.domain.services.series

import com.lowbudgetlcs.domain.models.Game
import com.lowbudgetlcs.domain.models.NewGame
import com.lowbudgetlcs.domain.models.NewSeries
import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.SeriesId
import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.events.Stage
import com.lowbudgetlcs.domain.models.team.TeamId

interface ISeriesService {
    /**
     * Create an event from a NewSeries.
     *
     * @param series new series details.
     * @return the newly created Series.
     *
     * @throws IllegalArgumentException if the series cannot be created.
     * @throws com.lowbudgetlcs.repositories.DatabaseException if the underlying repositories fail.
     */
    fun createSeries(series: NewSeries): Series

    /** Fetches all series from an event. */
    fun getAllSeriesFromEvent(id: EventId): List<Series>

    /**
     * Fetch a series by id.
     *
     * @param id the id of the series.
     * @return the specified series.
     *
     * @throws NoSuchElementException when the series is not found.
     * @throws com.lowbudgetlcs.repositories.DatabaseException when the underlying repository fails.
     */
    fun getSeries(id: SeriesId): Series

    /**
     * Return a series given two TeamIds and an event Stage. Will throw if multiple series match.
     *
     * @param eventId the event to search.
     * @param teamId1 the first teamId to filter by.
     * @param teamId2 the second teamId to filter by.
     * @param stage the event stage to filter by.
     * @return a series containing both team ids inside the specified event stage.
     *
     * @throws NoSuchElementException when no series is found.
     * @throws com.lowbudgetlcs.repositories.DatabaseException if >1 series is found.
     * @throws IllegalArgumentException when the teamIds are invalid.
     */
    fun findSeries(
        eventId: EventId,
        teamId1: TeamId,
        teamId2: TeamId,
        stage: Stage,
    ): Series

    /**
     * Remove a series.
     *
     * @param SeriesId the target series.
     *
     * @throws NoSuchElementException if the specified event or team doesn't exist
     * @throws com.lowbudgetlcs.repositories.DatabaseException if the delete operation fails
     */
    fun removeSeries(id: SeriesId)

    /**
     * Create a game inside of a series.
     *
     * @param NewGame the new game parameters.
     */
    suspend fun createGame(newGame: NewGame): Game
}
