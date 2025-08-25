package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.events.toEventId
import com.lowbudgetlcs.domain.models.team.TeamId
import com.lowbudgetlcs.domain.models.team.toTeamId
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL.*
import org.jooq.storage.tables.references.SERIES
import org.jooq.storage.tables.references.SERIES_RESULTS
import org.jooq.storage.tables.references.TEAM_TO_SERIES

class SeriesRepository(private val dsl: DSLContext) : ISeriesRepository {

    override fun getById(id: SeriesId): Series? =
        selectSeries().where(SERIES.ID.eq(id.value)).fetchOne()?.let(::rowToSeries)


    override fun getAllByEventId(id: EventId): List<Series> =
        selectSeries().where(SERIES.EVENT_ID.eq(id.value)).fetch().mapNotNull(::rowToSeries)

    override fun getByParticipantIds(
        team1Id: TeamId, team2Id: TeamId
    ): Series? =
        selectSeries().where(
            SERIES.ID.`in`(
                select(TEAM_TO_SERIES.SERIES_ID).from(TEAM_TO_SERIES)
                    .where(TEAM_TO_SERIES.TEAM_ID.`in`(team1Id.value, team2Id.value)).groupBy(TEAM_TO_SERIES.SERIES_ID)
                    .having(count().eq(2))
            )
        ).fetchOne()?.let(::rowToSeries)


    override fun insert(newSeries: NewSeries): Series? {
        val id = dsl.transactionResult { t ->
            val tx = t.dsl()
            val insertedId = tx.insertInto(
                SERIES
            ).set(SERIES.EVENT_ID, newSeries.eventId.value).set(SERIES.GAMES_TO_WIN, newSeries.gamesToWin)
                .returning(SERIES.ID).fetchOne()?.get(SERIES.ID)

            newSeries.participantIds.forEach { id ->
                tx.insertInto(TEAM_TO_SERIES).set(TEAM_TO_SERIES.SERIES_ID, insertedId)
                    .set(TEAM_TO_SERIES.TEAM_ID, id.value).execute()
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
            select(TEAM_TO_SERIES.TEAM_ID).from(TEAM_TO_SERIES).where(TEAM_TO_SERIES.SERIES_ID.eq(SERIES.ID))
        ).`as`("participants")
    }

    private fun selectSeries() = dsl.select(
        SERIES.ID,
        SERIES.GAMES_TO_WIN,
        SERIES.EVENT_ID,
        participants,
        SERIES_RESULTS.WINNER_TEAM_ID,
        SERIES_RESULTS.LOSER_TEAM_ID
    ).from(SERIES).leftJoin(SERIES_RESULTS).on(SERIES.ID.eq(SERIES_RESULTS.SERIES_ID))


    private fun rowToSeries(row: Record): Series? {
        // NOT NULL data
        val seriesId = row[SERIES.ID]?.toSeriesId() ?: return null
        val eventId = row[SERIES.EVENT_ID]?.toEventId() ?: return null
        val gamesToWin = row[SERIES.GAMES_TO_WIN] ?: return null
        val participants = row[participants].map { it.value1()?.toTeamId() }
        // potentially null data
        val winner = row[SERIES_RESULTS.WINNER_TEAM_ID]?.toTeamId()
        val loser = row[SERIES_RESULTS.LOSER_TEAM_ID]?.toTeamId()

        val s = Series(
            id = seriesId,
            eventId = eventId,
            gamesToWin = gamesToWin,
            participants = participants,
            result = if (winner != null && loser != null) SeriesResult(winner, loser) else null
        )
        return s
    }
}
