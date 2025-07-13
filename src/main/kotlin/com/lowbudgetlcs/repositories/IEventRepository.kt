package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.*

interface IEventRepository {
    fun getAll(): List<Event>
    fun getById(id: EventId): Event?
    fun insert(event: NewEvent, tournamentId: TournamentId): Event?
    fun updateStatusById(id: EventId, status: EventStatus): Event?
}