package com.lowbudgetlcs.domain.services.auth

import com.lowbudgetlcs.domain.models.auth.FreshAccessToken
import com.lowbudgetlcs.domain.models.auth.NewAccessToken
import com.lowbudgetlcs.domain.models.auth.Session
import com.lowbudgetlcs.domain.models.auth.User
import com.sksamuel.hoplite.Masked

interface IAuthService {
    fun authenticate(
        username: String,
        password: Masked,
    ): User

    fun authenticate(token: String): User

    fun createSession(user: User): Session

    fun clearSession(session: Session)

    fun validateSession(session: Session)

    fun createAccessToken(newToken: NewAccessToken): FreshAccessToken
}
