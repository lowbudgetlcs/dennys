package com.lowbudgetlcs.domain.services.user

import com.lowbudgetlcs.domain.models.auth.User
import com.lowbudgetlcs.domain.models.auth.types.UserId

interface IUserService {
    fun getUser(id: UserId): User
}
