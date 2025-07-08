package com.lowbudgetlcs.domain.models

import java.time.OffsetDateTime


data class Team(
    val id: Int,
    val name: String,
    val logoName: String?,
    val event: Event,
)

data class TeamAuditLog(
    val id: Int,
    val teamId: Int,
    val createdAt: OffsetDateTime,
    val action: String,
    val message: String,
    val origin: String
)