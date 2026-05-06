package com.lowbudgetlcs.domain.division.core.model

fun List<Event>.filterByName(query: EventQuery?): List<Event> =
    this.filter { if (query?.name == null) true else it.name.contains(query.name) }

fun List<Event>.filterByStatus(query: EventQuery?): List<Event> =
    this.filter { if (query?.status == null) true else it.status == query.status }
