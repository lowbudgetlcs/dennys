package com.lowbudgetlcs.domain.user

import com.lowbudgetlcs.domain.user.models.User
import com.lowbudgetlcs.domain.user.models.types.UserId
import com.lowbudgetlcs.logger
import com.lowbudgetlcs.repositories.user.IUserRepository

class UserService(
    private val repo: IUserRepository,
) : IUserService {

    override fun getUser(id: UserId): User {
        logger.debug("Getting user by '$id'...")
        return repo.getById(id) ?: throw NoSuchElementException("User with id '${id.value}' not found.")
    }
}
