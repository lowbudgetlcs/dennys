package com.lowbudgetlcs.repositories.series

import com.lowbudgetlcs.bridges.LblcsDatabaseBridge
import com.lowbudgetlcs.entities.DivisionId
import com.lowbudgetlcs.entities.Series
import com.lowbudgetlcs.entities.SeriesId
import com.lowbudgetlcs.entities.TeamId
import com.lowbudgetlcs.repositories.ICriteria

class SeriesRepositoryImpl : ISeriesRepository {
    private val lblcs = LblcsDatabaseBridge().db

    override fun create(entity: Series): Series {
        TODO("Not yet implemented")
    }

    override fun readAll(): List<Series> {
        TODO("Not yet implemented")
    }

    override fun readById(id: SeriesId): Series? = lblcs.seriesQueries.readById(id.id).executeAsOneOrNull()?.toSeries()

    override fun readByCriteria(criteria: ICriteria<Series>): List<Series> {
        TODO("Not yet implemented")
    }

    override fun update(entity: Series): Series =
        lblcs.seriesQueries.updateSeries(entity.winner?.id, entity.loser?.id, entity.id.id).executeAsOneOrNull().let {
            if (it != null) return it.toSeries()
            return entity
        }

    override fun delete(entity: Series): Series {
        TODO("Not yet implemented")
    }

    private fun migrations.Series.toSeries(): Series = Series(
        SeriesId(this.id),
        DivisionId(this.division_id),
        this.winner_id?.let { TeamId(it) },
        this.loser_id?.let { TeamId(it) },
    )
}