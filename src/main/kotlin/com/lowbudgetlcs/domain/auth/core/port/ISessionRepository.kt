package com.lowbudgetlcs.domain.auth.core.port

import com.lowbudgetlcs.domain.auth.core.models.NewSession
import com.lowbudgetlcs.domain.auth.core.models.Session
import com.lowbudgetlcs.domain.auth.core.models.types.SessionId

interface ISessionRepository {
    fun getAll(): List<Session>

    fun getById(id: SessionId): Session?

    fun insert(newSession: NewSession): Session?

    fun delete(id: SessionId)
}
