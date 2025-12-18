package com.lowbudgetlcs.api.auth

import com.lowbudgetlcs.domain.models.auth.Session
import com.lowbudgetlcs.domain.models.auth.toSessionId
import com.lowbudgetlcs.domain.models.auth.toUserId

fun UserSession.toSession(): Session = Session(id.toSessionId(), userId.toUserId(), expiresAt)

fun Session.toUserSession(): UserSession = UserSession(id.value, userId.value, expiresAt)
