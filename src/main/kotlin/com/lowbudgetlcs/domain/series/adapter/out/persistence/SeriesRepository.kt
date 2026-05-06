package com.lowbudgetlcs.domain.series.adapter.out.persistence

import com.lowbudgetlcs.domain.division.core.model.enums.EventStage
import com.lowbudgetlcs.domain.division.core.model.types.EventId
import com.lowbudgetlcs.domain.division.core.model.types.toEventId
import com.lowbudgetlcs.domain.series.core.model.NewSeries
import com.lowbudgetlcs.domain.series.core.model.Series
import com.lowbudgetlcs.domain.series.core.model.SeriesResult
import com.lowbudgetlcs.domain.series.core.model.types.SeriesId
import com.lowbudgetlcs.domain.series.core.model.types.toSeriesId
import com.lowbudgetlcs.domain.series.core.port.ISeriesRepository
import com.lowbudgetlcs.domain.team.core.model.types.TeamId
import com.lowbudgetlcs.domain.team.core.model.types.toTeamId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    override suspend fun getById(id: SeriesId): Series? =
        withContext(Dispatchers.IO) {
            selectSeries().where(SERIES.ID.eq(id.value)).fetchOne()
        }?.let(::rowToSeries)

    override suspend fun getAllByEventId(id: EventId): List<Series> =
        withContext(Dispatchers.IO) {
            selectSeries().where(SERIES.EVENT_ID.eq(id.value)).fetch()
        }.mapNotNull(::rowToSeries)

    override suspend fun insert(newSeries: NewSeries): Series? {
        val id =
            withContext(Dispatchers.IO) {
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

                    val insertChild = { id: TeamId ->
                        tx
                            .insertInto(TEAM_TO_SERIES)
                            .set(TEAM_TO_SERIES.SERIES_ID, insertedId)
                            .set(TEAM_TO_SERIES.TEAM_ID, id.value)
                            .execute()
                    }
                    insertChild(newSeries.participantIds.first)
                    insertChild(newSeries.participantIds.second)
                    insertedId
                }
            }
        return id?.toSeriesId()?.let { getById(it) }
    }

    // TEAM_TO_SERIES are defined with delete on cascade
    override suspend fun delete(id: SeriesId) {
        withContext(Dispatchers.IO) {
            dsl.delete(SERIES).where(SERIES.ID.eq(id.value)).execute()
        }
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
        val p = row[participants].mapNotNull { it.value1()?.toTeamId() }
        // potentially null data
        val winner = row[SERIES_RESULTS.WINNER_TEAM_ID]?.toTeamId()
        val loser = row[SERIES_RESULTS.LOSER_TEAM_ID]?.toTeamId()

        val s =
            Series(
                id = seriesId,
                eventId = eventId,
                eventStage = eventStage,
                totalGames = totalGames,
                participants = p[0] to p[1],
                result = if (winner != null && loser != null) SeriesResult(winner, loser) else null,
            )
        return s
    }
}
