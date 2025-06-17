package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.models.Series
import com.lowbudgetlcs.models.SeriesId

interface ISeriesRepository {
    /* Fetches a series via id from storage, returns null if none found */
    fun get(id: SeriesId): Series?

    /* Updates a series in storage, returns null if operation fails */
    fun update(entity: Series): Series?
}