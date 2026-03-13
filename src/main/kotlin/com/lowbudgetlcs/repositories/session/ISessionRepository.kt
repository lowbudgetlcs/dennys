package com.lowbudgetlcs.repositories.session

import com.lowbudgetlcs.domain.auth.models.NewSession
import com.lowbudgetlcs.domain.auth.models.Session
import com.lowbudgetlcs.domain.auth.models.types.SessionId

interface ISessionRepository {
    fun getAll(): List<Session>

    fun getById(id: SessionId): Session?

    fun insert(newSession: NewSession): Session?

    fun delete(id: SessionId)
}
