package com.lowbudgetlcs.repositories.series

import com.lowbudgetlcs.bridges.LblcsDatabaseBridge
import com.lowbudgetlcs.models.DivisionId
import com.lowbudgetlcs.models.Series
import com.lowbudgetlcs.models.SeriesId
import com.lowbudgetlcs.models.TeamId
import com.lowbudgetlcs.repositories.Criteria

class SeriesRepositoryImpl : SeriesRepository {
    private val lblcs = LblcsDatabaseBridge().db
    override fun readAll(): List<Series> {
        TODO("Not yet implemented")
    }

    override fun readByCriteria(criteria: Criteria<Series>): List<Series> {
        TODO("Not yet implemented")
    }

    override fun delete(entity: Series): Series {
        TODO("Not yet implemented")
    }

    override fun update(entity: Series): Series =
        lblcs.seriesQueries.updateSeries(entity.winner?.id, entity.loser?.id, entity.id.id).executeAsOneOrNull().let {
            if (it != null) return transform(it)
            return entity
        }

    override fun create(entity: Series): Series {
        TODO("Not yet implemented")
    }

    override fun readById(id: SeriesId): Series? =
        lblcs.seriesQueries.readById(id.id).executeAsOneOrNull()?.let { transform(it) }

    private fun transform(entity: migrations.Series): Series = Series(
        SeriesId(entity.id),
        DivisionId(entity.division_id),
        entity.winner_id?.let { TeamId(it) },
        entity.loser_id?.let { TeamId(it) },
        entity.playoffs
    )
}