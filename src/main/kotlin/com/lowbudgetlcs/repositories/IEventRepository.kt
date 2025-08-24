package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.events.Event
import com.lowbudgetlcs.domain.models.events.EventGroupId
import com.lowbudgetlcs.domain.models.events.EventId
import com.lowbudgetlcs.domain.models.events.EventWithTeams
import com.lowbudgetlcs.domain.models.events.NewEvent
import com.lowbudgetlcs.domain.models.team.TeamId
import com.lowbudgetlcs.domain.models.tournament.TournamentId

interface IEventRepository {
    fun getAll(): List<Event>
    fun getById(id: EventId): Event?
    fun getAllByGroupId(groupId: EventGroupId): List<Event>
    fun insert(newEvent: NewEvent, tournamentId: TournamentId): Event?
    fun update(event: Event): Event?
}