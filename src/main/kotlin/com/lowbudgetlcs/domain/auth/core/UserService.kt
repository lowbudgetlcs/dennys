package com.lowbudgetlcs.domain.auth.core

import com.lowbudgetlcs.domain.auth.core.port.IUserRepository
import com.lowbudgetlcs.domain.auth.core.port.IUserService
import com.lowbudgetlcs.domain.auth.core.models.User
import com.lowbudgetlcs.domain.auth.core.models.types.UserId
import com.lowbudgetlcs.logger

class UserService(
    private val repo: IUserRepository,
) : IUserService {

    override suspend fun getUser(id: UserId): User {
        logger.debug("Getting user by '$id'...")
        return repo.getById(id) ?: throw NoSuchElementException("User with id '${id.value}' not found.")
    }
}
