package com.lowbudgetlcs.domain.auth.core.port

import com.lowbudgetlcs.domain.auth.core.models.NewUser
import com.lowbudgetlcs.domain.auth.core.models.User
import com.lowbudgetlcs.domain.auth.core.models.types.UserId
import com.lowbudgetlcs.domain.auth.core.models.types.Username

interface IUserRepository {
    suspend fun getAll(): List<User>
    suspend fun getById(id: UserId): User?
    suspend fun getByUsername(username: Username): User?
    suspend fun insert(newUser: NewUser): User?
}
