package com.lowbudgetlcs.domain.auth.core.port

import com.lowbudgetlcs.domain.user.models.NewUser
import com.lowbudgetlcs.domain.user.models.User
import com.lowbudgetlcs.domain.user.models.types.UserId
import com.lowbudgetlcs.domain.user.models.types.Username

interface IUserRepository {
    fun getAll(): List<User>

    fun getById(id: UserId): User?

    fun getByUsername(username: Username): User?

    fun insert(newUser: NewUser): User?
}
