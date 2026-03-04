package com.lowbudgetlcs.domain.team.models.types

@JvmInline
value class TeamLogoName(
    val value: String,
) {
    init {
        require(value.isNotEmpty()) { "Team logo cannot be blank." }
    }
}
