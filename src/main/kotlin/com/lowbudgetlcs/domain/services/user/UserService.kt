package com.lowbudgetlcs.domain.services.user

import com.lowbudgetlcs.domain.models.auth.User
import com.lowbudgetlcs.domain.models.auth.UserId
import com.lowbudgetlcs.repositories.user.IUserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UserService(
    private val repo: IUserRepository,
) : IUserService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun getUser(id: UserId): User {
        logger.debug("Getting user by '$id'...")
        return repo.getById(id) ?: throw NoSuchElementException("User with id '${id.value}' not found.")
    }
}
