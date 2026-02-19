package com.lowbudgetlcs.domain.auth

import com.lowbudgetlcs.domain.auth.models.FreshAccessToken
import com.lowbudgetlcs.domain.auth.models.NewAccessToken
import com.lowbudgetlcs.domain.auth.models.Session
import com.lowbudgetlcs.domain.auth.models.User
import com.lowbudgetlcs.domain.auth.models.types.Username
import com.sksamuel.hoplite.Masked

interface IAuthService {
    suspend fun authenticate(
        username: Username,
        password: Masked,
    ): User

    suspend fun authenticate(token: String): User

    fun createSession(user: User): Session

    fun clearSession(session: Session)

    fun validateSession(session: Session)

    suspend fun createAccessToken(newToken: NewAccessToken): FreshAccessToken

    fun cleanupExpiredSessions()
}
