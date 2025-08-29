package com.lowbudgetlcs.repositories.series

import com.lowbudgetlcs.domain.models.NewSeries
import com.lowbudgetlcs.domain.models.Series
import com.lowbudgetlcs.domain.models.SeriesId
import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.team.TeamId

interface ISeriesRepository {
    fun insert(newSeries: NewSeries): Series?
    fun getById(id: SeriesId): Series?
    fun getAllByEventId(id: EventId): List<Series>
    fun getByParticipantIds(team1Id: TeamId, team2Id: TeamId): Series?
    fun delete(id: SeriesId)
}
