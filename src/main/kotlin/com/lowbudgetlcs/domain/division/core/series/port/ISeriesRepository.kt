package com.lowbudgetlcs.domain.division.core.series.port

import com.lowbudgetlcs.domain.division.core.event.model.types.EventId
import com.lowbudgetlcs.domain.division.core.series.model.NewSeries
import com.lowbudgetlcs.domain.division.core.series.model.Series
import com.lowbudgetlcs.domain.division.core.series.model.types.SeriesId

interface ISeriesRepository {
    suspend fun insert(newSeries: NewSeries): Series?
    suspend fun getById(id: SeriesId): Series?
    suspend fun getAllByEventId(id: EventId): List<Series>
    suspend fun delete(id: SeriesId)
}
