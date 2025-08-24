package com.lowbudgetlcs.repositories.jooq

import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.models.team.toTeamId
import com.lowbudgetlcs.domain.models.toSeriesId
import org.jooq.DSLContext
import org.jooq.storage.tables.Series.Companion.SERIES
import org.jooq.storage.tables.SeriesResults.Companion.SERIES_RESULTS

class JooqSeriesRepository(private val dsl: DSLContext) {

    fun findById(id: Int): Series? =
        dsl
            .select(
                SERIES.ID,
                SERIES.EVENT_ID,
                SERIES.GAMES_TO_WIN,
                SERIES_RESULTS.WINNER_TEAM_ID,
                SERIES_RESULTS.LOSER_TEAM_ID
            )
            .from(SERIES)
            .leftJoin(SERIES_RESULTS)
            .on(SERIES_RESULTS.SERIES_ID.eq(SERIES.ID))
            .where(SERIES.ID.eq(id))
            .fetchOne()?.let { row ->
                Series(
                    id = row[SERIES.ID]!!.toSeriesId(),
                    eventId = row[SERIES.EVENT_ID]!!.toEventId(),
                    gamesToWin = row[SERIES.GAMES_TO_WIN]!!,
                    winnerId = row[SERIES_RESULTS.WINNER_TEAM_ID]?.toTeamId(),
                    loserId = row[SERIES_RESULTS.LOSER_TEAM_ID]?.toTeamId()
                )
            }
}