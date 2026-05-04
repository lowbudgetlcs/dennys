package com.lowbudgetlcs.domain.auth.core.port

import com.lowbudgetlcs.domain.auth.core.models.NewSession
import com.lowbudgetlcs.domain.auth.core.models.Session
import com.lowbudgetlcs.domain.auth.core.models.types.SessionId

interface ISessionRepository {
    suspend fun getAll(): List<Session>
    suspend fun getById(id: SessionId): Session?
    suspend fun insert(newSession: NewSession): Session?
    suspend fun delete(id: SessionId)
}
