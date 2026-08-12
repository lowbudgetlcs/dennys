package com.lowbudgetlcs.domain.series

import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.series.game.models.NewTournamentCode
import com.lowbudgetlcs.domain.series.game.models.TournamentCode
import com.lowbudgetlcs.domain.series.game.models.types.TournamentCodeId
import com.lowbudgetlcs.domain.series.models.NewSeries
import com.lowbudgetlcs.domain.series.models.RefreshOutcome
import com.lowbudgetlcs.domain.series.models.ReportOutcome
import com.lowbudgetlcs.domain.series.models.ReportedResult
import com.lowbudgetlcs.domain.series.models.Series
import com.lowbudgetlcs.domain.series.models.SeriesWithGames
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.types.TeamId

interface ISeriesService {
    /**
     * Create an event from a NewSeries.
     *
     * @param series new series details.
     * @return the newly created Series.
     *
     * @throws IllegalArgumentException if the series cannot be created.
     * @throws com.lowbudgetlcs.repositories.DatabaseException if the underlying repositories fail.
     */
    fun createSeries(series: NewSeries): Series

    /** Fetches all series from an event. */
    fun getAllSeriesFromEvent(id: EventId): List<Series>

    /**
     * Fetch a series by id.
     *
     * @param id the id of the series.
     * @return the specified series.
     *
     * @throws NoSuchElementException when the series is not found.
     * @throws com.lowbudgetlcs.repositories.DatabaseException when the underlying repository fails.
     */
    fun getSeries(id: SeriesId): Series

    /**
     * Re-evaluate whether a series is finished and close it if so. Run on every
     * result write, whichever source it came from. No-op when the series is
     * already complete or was deliberately reopened.
     */
    fun evaluateCompletion(id: SeriesId): Series

    suspend fun refreshFromRiot(id: SeriesId): RefreshOutcome

    suspend fun refreshFromShortcode(shortcode: Shortcode): RefreshOutcome?

    /**
     * Re-check Riot for a single tournament code and record a game if one is found.
     *
     * Exactly one of [tournamentCodeId] or [shortcode] must be supplied — unlike
     * [reportResult], neither is not allowed, because this operation is defined by the code it
     * targets. A code that already has a game recorded is left untouched and reports
     * [RefreshOutcome.ANSWERED_EMPTY].
     *
     * @throws IllegalArgumentException if both or neither identifier is supplied.
     * @throws NoSuchElementException if the series or code does not exist, or the code belongs to
     *   another series.
     */
    suspend fun refreshFromCode(
        id: SeriesId,
        tournamentCodeId: TournamentCodeId?,
        shortcode: Shortcode?,
    ): RefreshOutcome

    suspend fun reportResult(
        id: SeriesId,
        report: ReportedResult,
    ): ReportOutcome

    fun completeSeries(
        id: SeriesId,
        winningTeamId: TeamId?,
        losingTeamId: TeamId?,
    ): Series

    fun reopenSeries(id: SeriesId): Series

    fun getSeriesWithGames(id: SeriesId): SeriesWithGames

    /**
     * Remove a series belonging to an event.
     *
     * @param eventId the event the series must belong to.
     * @param id the target series.
     *
     * @throws NoSuchElementException if the series doesn't exist or isn't part of the event
     * @throws IllegalStateException if the series has codes or games recorded against it
     * @throws com.lowbudgetlcs.repositories.DatabaseException if the delete operation fails
     */
    fun removeSeries(
        eventId: EventId,
        id: SeriesId,
    )

    /**
     * Create a game inside of a series.
     *
     * @param NewTournamentCode the new game parameters.
     */
    suspend fun createGame(newCode: NewTournamentCode): TournamentCode
}
