package com.lowbudgetlcs.domain.series

import com.lowbudgetlcs.domain.event.models.Shortcode
import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.series.game.models.NewTournamentCode
import com.lowbudgetlcs.domain.series.game.models.TournamentCode
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
     * Remove a series.
     *
     * @param SeriesId the target series.
     *
     * @throws NoSuchElementException if the specified event or team doesn't exist
     * @throws com.lowbudgetlcs.repositories.DatabaseException if the delete operation fails
     */
    fun removeSeries(id: SeriesId)

    /**
     * Create a game inside of a series.
     *
     * @param NewTournamentCode the new game parameters.
     */
    suspend fun createGame(newCode: NewTournamentCode): TournamentCode
}
