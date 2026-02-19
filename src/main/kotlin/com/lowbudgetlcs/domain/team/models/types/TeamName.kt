package com.lowbudgetlcs.domain.team.models.types

@JvmInline
value class TeamName(
    val value: String,
) {
    init {
        require(value.isNotEmpty()) { "Team name cannot be blank." }
        // TODO: Magic number- This should be configurable.
        require(value.length < 80) { "Team name must be less than 80 characters." }
    }
}
