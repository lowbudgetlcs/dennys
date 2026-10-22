package com.lowbudgetlcs.repositories.series

import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.SeriesResult
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.series.models.types.SeriesStatus

interface ISeriesRepository {
    fun insert(newSeries: NewSeries): Series?

    fun getById(id: SeriesId): Series?

    fun getAllByEventId(id: EventId): List<Series>

    fun delete(id: SeriesId)

    /**
     * Set the status and, for COMPLETE, the result. A [result] or [doubleLoss] replaces the stored
     * result; COMPLETE with neither keeps it; any other status clears it.
     */
    fun setStatus(
        id: SeriesId,
        status: SeriesStatus,
        result: SeriesResult? = null,
        doubleLoss: Boolean = false,
    ): Series?

    /** Move a NOT_STARTED series to IN_PROGRESS. Does nothing in any other status. */
    fun markInProgress(id: SeriesId)
}
