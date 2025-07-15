package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.domain.models.EventGroup

interface IEventGroupRepository {
    fun getAll(): List<EventGroup>
}