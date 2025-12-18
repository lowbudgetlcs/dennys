package com.lowbudgetlcs.domain.services.auth

import com.lowbudgetlcs.domain.models.auth.User
import com.lowbudgetlcs.domain.models.auth.UserSession

interface IAuthService {
    fun authenticate(
        username: String,
        password: String,
    ): User

    fun createSession(user: User): UserSession

    fun validateSession(session: UserSession): Boolean
}
