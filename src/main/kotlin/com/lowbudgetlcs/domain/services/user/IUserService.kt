package com.lowbudgetlcs.domain.services.user

import com.lowbudgetlcs.domain.models.auth.User

interface IUserService {
    fun getUser(id: Int): User
}
