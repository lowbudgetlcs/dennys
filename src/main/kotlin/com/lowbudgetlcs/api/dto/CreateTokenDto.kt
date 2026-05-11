package com.lowbudgetlcs.api.dto

import com.lowbudgetlcs.domain.auth.models.NewAccessToken
import com.lowbudgetlcs.domain.user.models.types.UserId
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
        // TODO: Use an enum. Will be implemented alongside stronger RBAC controls.
        scopes = scopes.split(":").toSet(),
        userId = userId,
    )
