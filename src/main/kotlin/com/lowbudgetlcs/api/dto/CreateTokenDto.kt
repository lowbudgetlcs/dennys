package com.lowbudgetlcs.api.dto

import com.lowbudgetlcs.domain.models.auth.NewAccessToken
import com.lowbudgetlcs.domain.models.auth.types.UserId
import com.lowbudgetlcs.serializers.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class CreateTokenDto(
    val name: String,
    @Serializable(with = InstantSerializer::class) val expiresAt: Instant,
    val scopes: String,
)

fun CreateTokenDto.toNewAccessToken(userId: UserId): NewAccessToken =
    NewAccessToken(
        name = name,
        expiresAt = expiresAt,
        scopes = scopes.split(":").toSet(),
        userId = userId,
    )
