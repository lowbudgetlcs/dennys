package com.lowbudgetlcs.repositories.tournamentcode

import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.series.game.models.NewTournamentCode
import com.lowbudgetlcs.domain.series.game.models.TournamentCode
import com.lowbudgetlcs.domain.series.game.models.types.TournamentCodeId
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import java.time.Instant

interface ITournamentCodeRepository {
    fun getById(id: TournamentCodeId): TournamentCode?

    fun getBySeriesId(id: SeriesId): List<TournamentCode>

    fun getByShortcode(shortcode: Shortcode): TournamentCode?

    /**
     * Codes worth asking Riot about: issued at or before [issuedBefore] and after the series' most
     * recent game, on a NOT_STARTED or IN_PROGRESS series in an ACTIVE event.
     */
    fun getOpenForPolling(issuedBefore: Instant): List<TournamentCode>

    fun insert(
        newCode: NewTournamentCode,
        shortcode: Shortcode,
    ): TournamentCode?
}
