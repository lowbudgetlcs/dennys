package com.lowbudgetlcs.repositories.session

import com.lowbudgetlcs.domain.models.auth.Session
import com.lowbudgetlcs.domain.models.auth.SessionId
import com.lowbudgetlcs.domain.models.auth.User

interface ISessionRepository {
    fun getById(id: SessionId): Session?

    fun insertSession(user: User): Session?

    fun delete(id: SessionId)
}
