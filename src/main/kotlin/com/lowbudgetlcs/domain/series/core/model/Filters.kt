package com.lowbudgetlcs.domain.series.core.model

import com.lowbudgetlcs.equalsIgnoreOrder

fun List<Series>.filterByStage(query: SeriesQuery?): List<Series> =
    this.filter { if (query?.eventStage == null) true else it.eventStage == query.eventStage }

fun List<Series>.filterByParticipants(query: SeriesQuery?): List<Series> {
    val participants = query?.teamIds
    return when {
        participants == null -> this
        participants.isEmpty() -> this
        participants.size == 1 -> this.filter { s -> s.participants.toList().any { participants.contains(it) } }
        else -> this.filter { s -> s.participants.toList().equalsIgnoreOrder(participants) }
    }
}
