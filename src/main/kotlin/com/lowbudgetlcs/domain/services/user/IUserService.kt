package com.lowbudgetlcs.domain.services.user

import com.lowbudgetlcs.domain.models.users.NewUser
import com.lowbudgetlcs.domain.models.users.Password
import com.lowbudgetlcs.domain.models.users.User
import com.lowbudgetlcs.domain.models.users.Username

interface IUserService {
    fun createUser(user: NewUser): User

    // TODO: Return a signed JWT
    fun login(
        username: Username,
        password: Password,
    ): Boolean?
}
