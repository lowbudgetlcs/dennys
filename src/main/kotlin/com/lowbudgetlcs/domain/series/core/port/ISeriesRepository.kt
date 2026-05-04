package com.lowbudgetlcs.domain.series.core.port

import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.series.core.model.NewSeries
import com.lowbudgetlcs.domain.series.core.model.Series
import com.lowbudgetlcs.domain.series.core.model.types.SeriesId

interface ISeriesRepository {
    suspend fun insert(newSeries: NewSeries): Series?
    suspend fun getById(id: SeriesId): Series?
    suspend fun getAllByEventId(id: EventId): List<Series>
    suspend fun delete(id: SeriesId)
}
