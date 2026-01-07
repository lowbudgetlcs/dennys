package com.lowbudgetlcs.repositories.session

import com.lowbudgetlcs.domain.models.auth.NewSession
import com.lowbudgetlcs.domain.models.auth.Session
import com.lowbudgetlcs.domain.models.auth.SessionId

interface ISessionRepository {
    fun getAll(): List<Session>

    fun getById(id: SessionId): Session?

    fun insert(newSession: NewSession): Session?

    fun delete(id: SessionId)
}
