package com.lowbudgetlcs.repositories.series

import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import java.time.Instant

interface ISeriesRepository {
    fun insert(newSeries: NewSeries): Series?

    fun getById(id: SeriesId): Series?

    fun getAllByEventId(id: EventId): List<Series>

    fun delete(id: SeriesId)

    fun complete(
        id: SeriesId,
        completedAt: Instant,
    ): Series?

    fun reopen(
        id: SeriesId,
        reopenedAt: Instant,
    ): Series?
}
