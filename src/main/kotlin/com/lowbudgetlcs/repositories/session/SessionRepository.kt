package com.lowbudgetlcs.repositories.session

import com.lowbudgetlcs.domain.auth.models.NewSession
import com.lowbudgetlcs.domain.auth.models.Session
import com.lowbudgetlcs.domain.auth.models.toSessionId
import com.lowbudgetlcs.domain.auth.models.toUserId
import com.lowbudgetlcs.domain.auth.models.types.SessionId
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.storage.tables.references.SESSIONS
import java.util.UUID

class SessionRepository(
    private val dsl: DSLContext,
) : ISessionRepository {
    override fun getAll(): List<Session> = selectSessions().fetch(::rowToSession)

    override fun getById(id: SessionId): Session? =
        selectSessions().where(SESSIONS.ID.eq(id.value)).fetchOne(::rowToSession)

    override fun insert(newSession: NewSession): Session? {
        val insertedId =
            dsl
                .insertInto(
                    SESSIONS,
                ).set(SESSIONS.ID, UUID.randomUUID())
                .set(SESSIONS.USER_ID, newSession.user.id.value)
                .set(SESSIONS.EXPIRES_AT, newSession.expiresAt)
                .returning(SESSIONS.ID)
                .fetchOne()
                ?.get(SESSIONS.ID)
        return insertedId?.toSessionId()?.let(::getById)
    }

    override fun delete(id: SessionId) {
        dsl.delete(SESSIONS).where(SESSIONS.ID.eq(id.value)).execute()
    }

    private fun selectSessions() =
        dsl
            .select(
                SESSIONS.ID,
                SESSIONS.USER_ID,
                SESSIONS.EXPIRES_AT,
            ).from(SESSIONS)

    fun rowToSession(row: Record): Session? {
        val sessionId = row[SESSIONS.ID]?.toSessionId() ?: return null
        val userId = row[SESSIONS.USER_ID]?.toUserId() ?: return null
        val expiresAt = row[SESSIONS.EXPIRES_AT] ?: return null
        return Session(
            id = sessionId,
            userId = userId,
            expiresAt = expiresAt,
        )
    }
}
