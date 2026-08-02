package com.lowbudgetlcs.repositories.tournamentcode

import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.series.game.models.NewTournamentCode
import com.lowbudgetlcs.domain.series.game.models.TournamentCode
import com.lowbudgetlcs.domain.series.game.models.types.TournamentCodeId

interface ITournamentCodeRepository {
    fun getById(id: TournamentCodeId): TournamentCode?

    fun insert(
        newCode: NewTournamentCode,
        shortcode: Shortcode,
    ): TournamentCode?
}
