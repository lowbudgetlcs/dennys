package com.lowbudgetlcs.repositories.user

import com.lowbudgetlcs.domain.auth.models.NewUser
import com.lowbudgetlcs.domain.auth.models.User
import com.lowbudgetlcs.domain.auth.models.types.UserId
import com.lowbudgetlcs.domain.auth.models.types.Username

interface IUserRepository {
    fun getAll(): List<User>

    fun getById(id: UserId): User?

    fun getByUsername(username: Username): User?

    fun insert(newUser: NewUser): User?
}
