package com.lowbudgetlcs.domain.auth.adapter.`in`.web.dto

import com.lowbudgetlcs.domain.auth.core.models.Session
import com.lowbudgetlcs.domain.auth.core.models.types.toSessionId
import com.lowbudgetlcs.domain.user.models.toUserId
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

// Extensions
fun UserSession.toSession(): Session = Session(id.toSessionId(), userId.toUserId(), expiresAt)
fun Session.toUserSession(): UserSession = UserSession(id.value, userId.value, expiresAt)
