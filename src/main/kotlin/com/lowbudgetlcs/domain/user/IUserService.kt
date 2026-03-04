package com.lowbudgetlcs.domain.user

import com.lowbudgetlcs.domain.user.models.User
import com.lowbudgetlcs.domain.user.models.types.UserId

interface IUserService {
    fun getUser(id: UserId): User
}
