package com.lowbudgetlcs.domain.services.user

import com.lowbudgetlcs.domain.auth.models.User
import com.lowbudgetlcs.domain.auth.models.types.UserId

interface IUserService {
    fun getUser(id: UserId): User
}
