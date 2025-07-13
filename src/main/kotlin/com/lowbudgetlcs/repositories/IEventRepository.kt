package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.Event
import com.lowbudgetlcs.domain.models.EventId
import com.lowbudgetlcs.domain.models.EventStatus
import com.lowbudgetlcs.domain.models.NewEvent
import com.lowbudgetlcs.domain.models.TournamentId

interface IEventRepository {
    fun getById(id: EventId): Event?
    fun insert(event: NewEvent, tournamentId: TournamentId): Event?
    fun updateStatusById(id: EventId, status: EventStatus): Event?
}