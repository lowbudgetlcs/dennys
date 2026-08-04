package com.lowbudgetlcs.repositories.tournamentcode

import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.event.models.toShortcode
import com.lowbudgetlcs.domain.series.game.models.NewTournamentCode
import com.lowbudgetlcs.domain.series.game.models.TournamentCode
import com.lowbudgetlcs.domain.series.game.models.toTournamentCodeId
import com.lowbudgetlcs.domain.series.game.models.types.TournamentCodeId
import com.lowbudgetlcs.domain.series.models.toSeriesId
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.toTeamId
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.TOURNAMENT_CODES

class TournamentCodeRepository(
    private val dsl: DSLContext,
) : ITournamentCodeRepository {
    override fun getById(id: TournamentCodeId) =
        selectCodes().where(TOURNAMENT_CODES.ID.eq(id.value)).fetchOne()?.let(::rowToTournamentCode)

    override fun getBySeriesId(id: SeriesId): List<TournamentCode> =
        selectCodes()
            .where(TOURNAMENT_CODES.SERIES_ID.eq(id.value))
            .orderBy(TOURNAMENT_CODES.ID)
            .fetch()
            .mapNotNull(::rowToTournamentCode)

    override fun getByShortcode(shortcode: Shortcode) =
        selectCodes().where(TOURNAMENT_CODES.SHORTCODE.eq(shortcode.value)).fetchOne()?.let(::rowToTournamentCode)

    override fun insert(
        newCode: NewTournamentCode,
        shortcode: Shortcode,
    ): TournamentCode? {
        val insertedId =
            dsl
                .insertInto(TOURNAMENT_CODES)
                .set(TOURNAMENT_CODES.SHORTCODE, shortcode.value)
                .set(TOURNAMENT_CODES.SERIES_ID, newCode.seriesId.value)
                .set(TOURNAMENT_CODES.BLUE_TEAM_ID, newCode.blueTeamId.value)
                .set(TOURNAMENT_CODES.RED_TEAM_ID, newCode.redTeamId.value)
                .returning(TOURNAMENT_CODES.ID)
                .fetchOne()
                ?.get(TOURNAMENT_CODES.ID)
        return insertedId?.toTournamentCodeId()?.let(::getById)
    }

    private fun selectCodes() =
        dsl
            .select(
                TOURNAMENT_CODES.ID,
                TOURNAMENT_CODES.SHORTCODE,
                TOURNAMENT_CODES.BLUE_TEAM_ID,
                TOURNAMENT_CODES.RED_TEAM_ID,
                TOURNAMENT_CODES.SERIES_ID,
                TOURNAMENT_CODES.CREATED_AT,
            ).from(TOURNAMENT_CODES)

    fun rowToTournamentCode(row: Record): TournamentCode? {
        val id = row[TOURNAMENT_CODES.ID]?.toTournamentCodeId() ?: return null
        val seriesId = row[TOURNAMENT_CODES.SERIES_ID]?.toSeriesId() ?: return null
        val blueTeamId = row[TOURNAMENT_CODES.BLUE_TEAM_ID]?.toTeamId() ?: return null
        val redTeamId = row[TOURNAMENT_CODES.RED_TEAM_ID]?.toTeamId() ?: return null
        val shortcode = row[TOURNAMENT_CODES.SHORTCODE]?.toShortcode() ?: return null
        val createdAt = row[TOURNAMENT_CODES.CREATED_AT] ?: return null

        return TournamentCode(
            id = id,
            shortcode = shortcode,
            blueTeamId = blueTeamId,
            redTeamId = redTeamId,
            seriesId = seriesId,
            createdAt = createdAt,
        )
    }
}
