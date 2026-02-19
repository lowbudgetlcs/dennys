package com.lowbudgetlcs.repositories.series

import com.lowbudgetlcs.domain.event.models.toEventId
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.models.NewSeries
import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.SeriesId
import com.lowbudgetlcs.domain.models.SeriesResult
import com.lowbudgetlcs.domain.models.team.toTeamId
import com.lowbudgetlcs.domain.models.toSeriesId
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.select
import org.jooq.storage.tables.references.SERIES
import org.jooq.storage.tables.references.SERIES_RESULTS
import org.jooq.storage.tables.references.TEAM_TO_SERIES

class SeriesRepository(
    private val dsl: DSLContext,
) : ISeriesRepository {
    override fun getById(id: SeriesId): Series? =
        selectSeries().where(SERIES.ID.eq(id.value)).fetchOne()?.let(::rowToSeries)

    override fun getAllByEventId(id: EventId): List<Series> =
        selectSeries().where(SERIES.EVENT_ID.eq(id.value)).fetch().mapNotNull(::rowToSeries)

    override fun insert(newSeries: NewSeries): Series? {
        val id =
            dsl.transactionResult { t ->
                val tx = t.dsl()
                val insertedId =
                    tx
                        .insertInto(
                            SERIES,
                        ).set(SERIES.EVENT_ID, newSeries.eventId.value)
                        .set(SERIES.TOTAL_GAMES, newSeries.totalGames)
                        .set(SERIES.STAGE, newSeries.eventStage.name)
                        .returning(SERIES.ID)
                        .fetchOne()
                        ?.get(SERIES.ID)

                newSeries.participantIds.forEach { id ->
                    tx
                        .insertInto(TEAM_TO_SERIES)
                        .set(TEAM_TO_SERIES.SERIES_ID, insertedId)
                        .set(TEAM_TO_SERIES.TEAM_ID, id.value)
                        .execute()
                }
                insertedId
            }
        return id?.toSeriesId()?.let(::getById)
    }

    // TEAM_TO_SERIES are defined with delete on cascade
    override fun delete(id: SeriesId) {
        dsl.delete(SERIES).where(SERIES.ID.eq(id.value)).execute()
    }

    // Typed multiset to select all teams associated with a series.
    val participants by lazy {
        multiset(
            select(TEAM_TO_SERIES.TEAM_ID).from(TEAM_TO_SERIES).where(TEAM_TO_SERIES.SERIES_ID.eq(SERIES.ID)),
        ).`as`("participants")
    }

    private fun selectSeries() =
        dsl
            .select(
                SERIES.ID,
                SERIES.TOTAL_GAMES,
                SERIES.EVENT_ID,
                SERIES.STAGE,
                participants,
                SERIES_RESULTS.WINNER_TEAM_ID,
                SERIES_RESULTS.LOSER_TEAM_ID,
            ).from(SERIES)
            .leftJoin(SERIES_RESULTS)
            .on(SERIES.ID.eq(SERIES_RESULTS.SERIES_ID))

    private fun rowToSeries(row: Record): Series? {
        // NOT NULL data
        val seriesId = row[SERIES.ID]?.toSeriesId() ?: return null
        val eventId = row[SERIES.EVENT_ID]?.toEventId() ?: return null
        val eventStage = row[SERIES.STAGE]?.let { EventStage.valueOf(it) } ?: return null
        val totalGames = row[SERIES.TOTAL_GAMES] ?: return null
        val participants = row[participants].mapNotNull { it.value1()?.toTeamId() }
        // potentially null data
        val winner = row[SERIES_RESULTS.WINNER_TEAM_ID]?.toTeamId()
        val loser = row[SERIES_RESULTS.LOSER_TEAM_ID]?.toTeamId()

        val s =
            Series(
                id = seriesId,
                eventId = eventId,
                eventStage = eventStage,
                totalGames = totalGames,
                participants = participants,
                result = if (winner != null && loser != null) SeriesResult(winner, loser) else null,
            )
        return s
    }
}
