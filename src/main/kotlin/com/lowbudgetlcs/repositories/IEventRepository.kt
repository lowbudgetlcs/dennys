package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.events.Event
import com.lowbudgetlcs.domain.models.events.group.EventGroupId
import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.events.NewEvent
import com.lowbudgetlcs.domain.models.riot.tournament.RiotTournamentId

interface IEventRepository {
    fun getAll(): List<Event>
    fun getById(id: EventId): Event?
    fun getAllByGroupId(groupId: EventGroupId): List<Event>
    fun insert(newEvent: NewEvent, riotTournamentId: RiotTournamentId): Event?
    fun update(event: Event): Event?
}