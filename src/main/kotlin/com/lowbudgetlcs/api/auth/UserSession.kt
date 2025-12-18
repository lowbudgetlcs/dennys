package com.lowbudgetlcs.api.auth

import com.lowbudgetlcs.serializers.InstantSerializer
import com.lowbudgetlcs.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class UserSession(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val userId: Int,
    @Serializable(with = InstantSerializer::class)
    val expiresAt: Instant,
)
