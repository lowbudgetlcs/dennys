package com.lowbudgetlcs.domain.models

import java.time.OffsetDateTime

data class TeamAuditEntry(
    val id: Int,
    val teamId: Int,
    val createdAt: OffsetDateTime,
    val action: String,
    val message: String,
    val origin: String
)
