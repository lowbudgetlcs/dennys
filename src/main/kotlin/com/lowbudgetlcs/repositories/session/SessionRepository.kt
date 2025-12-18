package com.lowbudgetlcs.repositories.session

import com.lowbudgetlcs.domain.models.auth.Session
import com.lowbudgetlcs.domain.models.auth.SessionId
import com.lowbudgetlcs.domain.models.auth.User

class SessionRepository : ISessionRepository {
    override fun getById(id: SessionId): Session? {
        TODO("Not yet implemented")
    }

    override fun insertSession(user: User): Session {
        TODO("Not yet implemented")
    }

    override fun delete(id: SessionId) {
        TODO("Not yet implemented")
    }
}
