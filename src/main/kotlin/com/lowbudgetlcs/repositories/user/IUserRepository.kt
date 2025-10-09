package com.lowbudgetlcs.repositories.user

import com.lowbudgetlcs.domain.models.users.NewUser
import com.lowbudgetlcs.domain.models.users.Password
import com.lowbudgetlcs.domain.models.users.User
import com.lowbudgetlcs.domain.models.users.UserId
import com.lowbudgetlcs.domain.models.users.Username

interface IUserRepository {
    fun getById(id: UserId): User?

    fun getByName(username: Username): User?

    /** IMPORTANT: The password in newUser is INTENTIONALLY IGNORED!!! */
    fun insert(
        newUser: NewUser,
        passwordHash: String,
        salt: String,
    ): User?

    fun getPasswordById(id: UserId): Password?
}
