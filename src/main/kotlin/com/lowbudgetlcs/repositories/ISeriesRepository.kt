package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.domain.models.events.EventId

interface ISeriesRepository {
    fun insert(newSeries: NewSeries): Series?
    fun getById(id: SeriesId): Series?
    fun getAllByEventId(id: EventId): List<Series>
    fun delete(id: SeriesId)
}
