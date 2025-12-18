package com.lowbudgetlcs.domain.services.user

import com.lowbudgetlcs.domain.models.auth.User

class UserService : IUserService {
    override fun getUser(id: Int): User {
        if (id == 1) return User(1, "ruuffian")
        throw NoSuchElementException("User with id '$id' does not exist.")
    }
}
