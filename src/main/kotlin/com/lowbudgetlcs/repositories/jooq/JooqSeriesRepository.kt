package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.series.Series
import org.jooq.DSLContext
import org.jooq.storage.tables.SeriesResults

class JooqSeriesRepository(private val dsl: DSLContext) {

    fun findById(id: Int): Series? =
        dsl
            .select(
                org.jooq.storage.tables.Series.Companion.SERIES.ID, org.jooq.storage.tables.Series.Companion.SERIES.EVENT_ID, org.jooq.storage.tables.Series.Companion.SERIES.GAMES_TO_WIN,
                SeriesResults.Companion.SERIES_RESULTS.WINNER_TEAM_ID, SeriesResults.Companion.SERIES_RESULTS.LOSER_TEAM_ID
            )
            .from(org.jooq.storage.tables.Series.Companion.SERIES)
            .leftJoin(SeriesResults.Companion.SERIES_RESULTS)
            .on(SeriesResults.Companion.SERIES_RESULTS.SERIES_ID.eq(org.jooq.storage.tables.Series.Companion.SERIES.ID))
            .where(org.jooq.storage.tables.Series.Companion.SERIES.ID.eq(id))
            .fetchOne()?.let { row ->
                Series(
                    id = row[org.jooq.storage.tables.Series.Companion.SERIES.ID]!!,
                    eventId = row[org.jooq.storage.tables.Series.Companion.SERIES.EVENT_ID]!!,
                    gamesToWin = row[org.jooq.storage.tables.Series.Companion.SERIES.GAMES_TO_WIN]!!,
                    winnerId = row[SeriesResults.Companion.SERIES_RESULTS.WINNER_TEAM_ID],
                    loserId = row[SeriesResults.Companion.SERIES_RESULTS.LOSER_TEAM_ID],
                )
            }
}