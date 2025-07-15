package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.*
import com.lowbudgetlcs.repositories.IEventGroupRepository
import com.lowbudgetlcs.repositories.IEventRepository
import com.lowbudgetlcs.repositories.ITournamentRepository

class EventService(
    private val eventRepo: IEventRepository,
    private val eventGroupRepo: IEventGroupRepository,
    private val tournamentRepo: ITournamentRepository
) {
    fun create(event: NewEvent, tournament: NewTournament): Event? = tournamentRepo.create(tournament)?.let {
        eventRepo.insert(event, it.id)
    }

    fun getEvents(): List<Event> = eventRepo.getAll()

    fun getEventsByGroupId(group: EventGroupId): List<Event> = eventRepo.getAllByGroupId(group)

    fun getEventGroups(): List<EventGroup> = eventGroupRepo.getAll()

    fun getEventGroupById(id: EventGroupId): EventGroup? = TODO()

    fun getEvent(id: EventId): Event? = eventRepo.getById(id)

    fun changeEventStatus(id: EventId, newStatus: EventStatus): Event? = eventRepo.updateStatusById(id, newStatus)
}