package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.NewSeries
import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.SeriesId
import com.lowbudgetlcs.domain.models.events.EventId

interface ISeriesService {

    /**
     * Create an event from a NewSeries.
     *
     * @param NewSeries event details.
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
     * @param SeriesId the id of the event.
     * @return the specified series.
     *
     * @throws NoSuchElementException when the series is not found.
     * @throws com.lowbudgetlcs.repositories.DatabaseException when the underlying repository fails.
     */
    fun getSeries(id: SeriesId): Series

    /**
     * Remove a series.
     *
     * @param SeriesId the target series.
     * @return the event with all registered teams.
     *
     * @throws NoSuchElementException if the specified event or team doesn't exist
     */
    fun removeSeries(id: SeriesId): Series
}
