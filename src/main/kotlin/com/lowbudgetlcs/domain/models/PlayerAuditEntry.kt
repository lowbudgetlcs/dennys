package com.lowbudgetlcs.domain.models

import java.time.OffsetDateTime

data class PlayerAuditEntry(
    val id: Int,
    val playerId: Int,
    val createdAt: OffsetDateTime,
    val action: String,
    val message: String,
    val origin: String
)
