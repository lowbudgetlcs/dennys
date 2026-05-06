package com.lowbudgetlcs.domain.division.core.application.queries

import com.lowbudgetlcs.domain.division.core.event.model.EventWithSeries
import com.lowbudgetlcs.domain.division.core.event.model.toEventWithSeries
import com.lowbudgetlcs.domain.division.core.event.model.types.EventId
import com.lowbudgetlcs.domain.division.core.event.port.IEventRepository
import com.lowbudgetlcs.domain.division.core.series.model.SeriesQuery
import com.lowbudgetlcs.domain.division.core.series.model.filterByParticipants
import com.lowbudgetlcs.domain.division.core.series.model.filterByStage
import com.lowbudgetlcs.domain.division.core.series.services.SeriesService
import com.lowbudgetlcs.logger

class GetEventWithSeriesQuery(private val eventRepo: IEventRepository, private val seriesService: SeriesService) {
    /**
     * Fetches all events and includes series that are registered to the event
     *
     * @param EventId the event to fetch.
     * @return the specified event with all child series.
     *
     * @throws NoSuchElementException if the specified event cannot be found
     */
    suspend fun execute(
        id: EventId,
        query: SeriesQuery? = null,
    ): EventWithSeries {
        logger.debug("Getting event by '$id' (with series)...")
        val event = eventRepo.getById(id) ?: throw NoSuchElementException("Event with id '${id.value}' not found.")
        val series = seriesService.getAllSeriesFromEvent(id).filterByStage(query).filterByParticipants(query)
        return event.toEventWithSeries(series)
    }
}
