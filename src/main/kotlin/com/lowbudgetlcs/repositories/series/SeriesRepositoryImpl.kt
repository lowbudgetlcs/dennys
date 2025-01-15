package com.lowbudgetlcs.repositories.series

import com.lowbudgetlcs.bridges.LblcsDatabaseBridge
import com.lowbudgetlcs.repositories.Repository
import migrations.Series

class SeriesRepositoryImpl : SeriesRepository, Repository<Series, Int> {
    private val lblcs = LblcsDatabaseBridge().db
    override fun readAll(): List<Series> {
        TODO("Not yet implemented")
    }

    override fun delete(entity: Series): Series {
        TODO("Not yet implemented")
    }

    override fun update(entity: Series): Series {
        TODO("Not yet implemented")
    }

    override fun create(entity: Series): Series {
        TODO("Not yet implemented")
    }

    override fun readById(id: Int) = lblcs.seriesQueries.readById(id).executeAsOneOrNull()

    override fun updateWinnerLoserById(winnerId: Int, loserId: Int, seriesId: Int) =
        lblcs.seriesQueries.updateWinnerLoserById(winner_id = winnerId, loser_id = loserId, id = seriesId)
            .executeAsOneOrNull() != null
}