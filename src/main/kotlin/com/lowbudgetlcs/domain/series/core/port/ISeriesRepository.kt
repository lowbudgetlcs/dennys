package com.lowbudgetlcs.domain.series.core.port

import com.lowbudgetlcs.domain.event.core.model.types.EventId
import com.lowbudgetlcs.domain.series.core.model.NewSeries
import com.lowbudgetlcs.domain.series.core.model.Series
import com.lowbudgetlcs.domain.series.core.model.types.SeriesId

interface ISeriesRepository {
    fun insert(newSeries: NewSeries): Series?

    fun getById(id: SeriesId): Series?

    fun getAllByEventId(id: EventId): List<Series>

    fun delete(id: SeriesId)
}
