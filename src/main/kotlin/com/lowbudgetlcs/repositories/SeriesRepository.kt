package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.Series
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.SERIES
import org.jooq.storage.tables.references.SERIES_RESULTS
import org.jooq.storage.tables.references.TEAM_TO_SERIES



class SeriesRepository(
    private val dsl: DSLContext
) : ISeriesRepository {

    override fun insert(newSeries: NewSeries): Series? {
        val insertedId = dsl.insertInto(SERIES)
            .set(SERIES.EVENT_ID, newSeries.eventId)
            .returning(SERIES.ID)
            .fetchOne()
            ?.get(SERIES.ID)

        dsl.insertInto(SERIES_RESULTS)
            .set(SERIES_RESULTS.SERIES_ID, insertedId)
            .execute()

        newSeries.particpantIds.forEach { id ->
            dsl.insertInto(TEAM_TO_SERIES)
                .set(TEAM_TO_SERIES.SERIES_ID, insertedId)
                .set(TEAM_TO_SERIES.TEAM_ID, id)
                .execute()
        }

        return insertedId?.toSeriesId()?.let(::getById)
    }

    override fun getAllFromEvent(eventId: EventId): List<Series> {
        return dsl.selectFrom(SERIES)
            .where(SERIES.EVENT_ID.eq(eventId.value))
            .fetchInto(Series::class.java)
    }

    override fun getById(id: PlayerId): PlayerWithAccounts? {
        return getSeriesRowById(id)
    }

    override fun removeSeries(seriesId: SeriesId): Series? {

        val updated = dsl.update(SERIES)
            .set(SERIES.id, null as Int?)
            .where(SERIES.ID.eq(seriesId.value))
            .execute()

        dsl.update(SERIES_RESULTS)
            .set(SERIES_RESULTS.SERIES_ID, null as Int?)
            .where(SERIES_RESULTS.SERIES_ID.eq(seriesId.value))
            .execute()

        dsl.update(TEAM_TO_SERIES)
            .set(TEAM_TO_SERIES.SERIES_ID, null as Int?)
            .where(TEAM_TO_SERIES.SERIES_ID.eq(seriesId.value))
            .execute()

        return if (updated > 0) getById(playerId) else null
    }
}
