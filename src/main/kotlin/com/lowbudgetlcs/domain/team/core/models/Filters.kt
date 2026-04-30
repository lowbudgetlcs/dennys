package com.lowbudgetlcs.domain.team.core.models

fun List<Team>.filterByString(query: TeamQuery?): List<Team> =
    this.filter { if (query?.name == null) true else it.name.contains(query.name) }
