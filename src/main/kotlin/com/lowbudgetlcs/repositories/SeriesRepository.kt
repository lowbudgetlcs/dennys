package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.NewSeries
import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.SeriesId
import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.toSeriesId
import org.jooq.DSLContext
import org.jooq.storage.tables.references.SERIES
import org.jooq.storage.tables.references.SERIES_RESULTS
import org.jooq.storage.tables.references.TEAM_TO_SERIES

class SeriesRepository(private val dsl: DSLContext) : ISeriesRepository {

    override fun insert(newSeries: NewSeries): Series? {
        val insertedId =
                dsl.insertInto(SERIES)
                        .set(SERIES.EVENT_ID, newSeries.eventId.value)
                        .returning(SERIES.ID)
                        .fetchOne()
                        ?.get(SERIES.ID)

        dsl.insertInto(SERIES_RESULTS).set(SERIES_RESULTS.SERIES_ID, insertedId).execute()

        newSeries.participantIds.forEach { id ->
            dsl.insertInto(TEAM_TO_SERIES)
                    .set(TEAM_TO_SERIES.SERIES_ID, insertedId)
                    .set(TEAM_TO_SERIES.TEAM_ID, id.value)
                    .execute()
        }

        return insertedId?.toSeriesId()?.let(::getById)
    }

    override fun getAllFromEvent(id: EventId): List<Series> {
        return dsl.selectFrom(SERIES)
                .where(SERIES.EVENT_ID.eq(id.value))
                .fetchInto(Series::class.java)
    }

    override fun getById(id: SeriesId): Series? {
        return dsl.selectFrom(SERIES).where(SERIES.ID.eq(id.value)).fetchOneInto(Series::class.java)
    }

    override fun removeSeries(id: SeriesId): Series? {

        val updated =
                dsl.update(SERIES)
                        .set(SERIES.ID, null as Int?)
                        .where(SERIES.ID.eq(id.value))
                        .execute()

        dsl.update(SERIES_RESULTS)
                .set(SERIES_RESULTS.SERIES_ID, null as Int?)
                .where(SERIES_RESULTS.SERIES_ID.eq(id.value))
                .execute()

        dsl.update(TEAM_TO_SERIES)
                .set(TEAM_TO_SERIES.SERIES_ID, null as Int?)
                .where(TEAM_TO_SERIES.SERIES_ID.eq(id.value))
                .execute()

        return if (updated > 0) getById(id) else null
    }
}
