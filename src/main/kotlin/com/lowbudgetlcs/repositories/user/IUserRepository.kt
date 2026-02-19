package com.lowbudgetlcs.repositories.user

import com.lowbudgetlcs.domain.models.auth.NewUser
import com.lowbudgetlcs.domain.models.auth.User
import com.lowbudgetlcs.domain.models.auth.types.UserId
import com.lowbudgetlcs.domain.models.auth.types.Username

interface IUserRepository {
    fun getAll(): List<User>

    fun getById(id: UserId): User?

    fun getByUsername(username: Username): User?

    fun insert(newUser: NewUser): User?
}
