package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.event.Event
import com.lowbudgetlcs.domain.models.event.NewEvent
import com.lowbudgetlcs.domain.tournament.Tournament

interface IEventRepository {
    fun getById(id: Int): Event?
    fun insert(event: NewEvent, tournament: Tournament): Event?
}