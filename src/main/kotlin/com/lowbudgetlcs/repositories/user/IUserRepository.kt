package com.lowbudgetlcs.repositories.user

import com.lowbudgetlcs.domain.models.auth.NewUser
import com.lowbudgetlcs.domain.models.auth.User
import com.lowbudgetlcs.domain.models.auth.UserId

interface IUserRepository {
    fun getAll(): List<User>

    fun getById(id: UserId): User?

    fun getByUsername(username: String): User?

    fun insert(newUser: NewUser): User?
}
