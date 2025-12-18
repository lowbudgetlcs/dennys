package com.lowbudgetlcs.domain.services.auth

import com.lowbudgetlcs.domain.models.auth.User
import com.lowbudgetlcs.domain.models.auth.UserSession

class AuthService : IAuthService {
    override fun authenticate(username: String, password: String): User {
        if (username == "ruuffian" && password == "changeit")
            return User(1, "ruuffian")
        throw UnauthorizedException("Invalid credentials.")
    }

    // This does NOT authenticate the user- it should only be called with
    // pre-authenticated users.
    override fun createSession(user: User): UserSession {
        return UserSession(
            sessionId = 10,
            userId = 1,
            username = "ruuffian"
        )
    }

    override fun validateSession(session: UserSession): Boolean {
        return session.sessionId == 10
    }

}
