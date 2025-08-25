package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.domain.models.events.EventId

interface ISeriesRepository {
    fun insert(newSeries: NewSeries): Series?
    fun getAllFromEvent(id: EventId): List<Series>
    fun getById(id: SeriesId): Series?

    fun removeSeries(id: SeriesId): Series?
}
