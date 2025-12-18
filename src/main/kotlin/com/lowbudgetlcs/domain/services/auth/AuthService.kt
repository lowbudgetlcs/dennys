package com.lowbudgetlcs.domain.services.auth

import com.lowbudgetlcs.api.auth.IPasswordHasher
import com.lowbudgetlcs.domain.models.auth.Session
import com.lowbudgetlcs.domain.models.auth.User
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.session.ISessionRepository
import com.lowbudgetlcs.repositories.user.IUserRepository
import com.sksamuel.hoplite.Masked
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AuthService(
    private val sessionRepo: ISessionRepository,
    private val userRepo: IUserRepository,
    private val hasher: IPasswordHasher,
) : IAuthService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun authenticate(
        username: String,
        password: Masked,
    ): User {
        logger.debug("Authenticating $username...")
        val user = userRepo.getByUsername(username) ?: throw UnauthorizedException("Invalid credentials.")
        logger.debug("Found user {}.", user)
        if (hasher.verify(
                password.value,
                user.passwordHash,
            )
        ) {
            logger.debug("Checking if user is active...")
            if (user.isActive) {
                return user
            } else {
                throw UnauthorizedException("Inactive user.")
            }
        } else {
            throw UnauthorizedException("Invalid credentials.")
        }
    }

    override fun createSession(user: User): Session {
        logger.debug("Starting session for '${user.username}'...")
        if (user.isActive) return sessionRepo.insertSession(user) ?: throw DatabaseException("An unkown error occured.")
        throw UnauthorizedException("This user is locked.")
    }

    override fun clearSession(session: Session) {
        sessionRepo.delete(session.id)
    }

    override fun validateSession(session: Session): Boolean {
        logger.debug("Validating session ${session.id}...")
        val saved = sessionRepo.getById(session.id) ?: throw UnauthorizedException("Invalid session id.")
        return saved.userId == session.userId
    }
}
