package com.lowbudgetlcs.repositories.series

import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.models.NewSeries
import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.SeriesId

interface ISeriesRepository {
    fun insert(newSeries: NewSeries): Series?

    fun getById(id: SeriesId): Series?

    fun getAllByEventId(id: EventId): List<Series>

    fun delete(id: SeriesId)
}
