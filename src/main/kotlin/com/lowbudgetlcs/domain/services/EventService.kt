package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.event.Event
import com.lowbudgetlcs.domain.models.event.NewEvent
import com.lowbudgetlcs.domain.tournament.NewTournament
import com.lowbudgetlcs.repositories.IEventRepository
import com.lowbudgetlcs.repositories.ITournamentRepository

class EventService(private val eventRepo: IEventRepository, private val riotTournamentRepo: ITournamentRepository) {
    fun create(event: NewEvent, tournament: NewTournament): Event? =
        riotTournamentRepo.create(tournament)?.let {
            eventRepo.insert(event, it)
        }
}