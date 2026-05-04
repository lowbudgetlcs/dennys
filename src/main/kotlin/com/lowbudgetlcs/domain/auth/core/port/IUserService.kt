package com.lowbudgetlcs.domain.auth.core.port

import com.lowbudgetlcs.domain.auth.core.models.User
import com.lowbudgetlcs.domain.auth.core.models.types.UserId

interface IUserService {
    suspend fun getUser(id: UserId): User
}
