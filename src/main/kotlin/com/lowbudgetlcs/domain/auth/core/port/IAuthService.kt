package com.lowbudgetlcs.domain.auth.core.port

import com.lowbudgetlcs.domain.auth.core.models.FreshAccessToken
import com.lowbudgetlcs.domain.auth.core.models.NewAccessToken
import com.lowbudgetlcs.domain.auth.core.models.Session
import com.lowbudgetlcs.domain.auth.core.models.User
import com.lowbudgetlcs.domain.auth.core.models.types.Username
import com.sksamuel.hoplite.Masked

interface IAuthService {
    suspend fun authenticate(
        username: Username,
        password: Masked,
    ): User
    suspend fun authenticate(token: String): User
    suspend fun createSession(user: User): Session
    suspend fun clearSession(session: Session)
    suspend fun validateSession(session: Session)
    suspend fun createAccessToken(newToken: NewAccessToken): FreshAccessToken
    suspend fun cleanupExpiredSessions()
}
