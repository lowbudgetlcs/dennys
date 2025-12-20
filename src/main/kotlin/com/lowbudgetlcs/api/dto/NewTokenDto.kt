package com.lowbudgetlcs.api.dto

import com.lowbudgetlcs.domain.models.auth.NewAccessToken
import com.lowbudgetlcs.domain.models.auth.UserId
import com.lowbudgetlcs.serializers.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class NewTokenDto(
    val name: String,
    @Serializable(with = InstantSerializer::class) val expiresAt: Instant,
    val scopes: String,
)

fun NewTokenDto.toNewAccessToken(userId: UserId): NewAccessToken =
    NewAccessToken(
        name = name,
        expiresAt = expiresAt,
        scopes = scopes.split(":").toSet(),
        userId = userId,
    )
