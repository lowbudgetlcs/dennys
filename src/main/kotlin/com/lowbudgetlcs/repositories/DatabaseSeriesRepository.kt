package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.Database
import com.lowbudgetlcs.models.DivisionId
import com.lowbudgetlcs.models.Series
import com.lowbudgetlcs.models.SeriesId
import com.lowbudgetlcs.models.TeamId

class DatabaseSeriesRepository(private val lblcs: Database) : ISeriesRepository {

    /**
     * Returns a [Series] derived from [migrations.Series].
     */
    private fun migrations.Series.toSeries(): Series = Series(
        SeriesId(this.id),
        DivisionId(this.division_id),
        this.winner_id?.let { TeamId(it) },
        this.loser_id?.let { TeamId(it) },
    )

    override fun get(id: SeriesId): Series? = lblcs.seriesQueries.readById(id.id).executeAsOneOrNull()?.toSeries()

    override fun update(entity: Series): Series? =
        lblcs.seriesQueries.updateSeries(entity.winner?.id, entity.loser?.id, entity.id.id).executeAsOneOrNull()
            ?.toSeries()
}