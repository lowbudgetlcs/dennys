package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.*

interface ISeriesRepository {
    fun insert(newSeries: NewSeries): Series?
    fun getAll(): List<Series>
    fun getById(id: SeriesId): Series?

    fun removeSeries(id: SeriesId): Series?
}
