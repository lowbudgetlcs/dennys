package com.lowbudgetlcs.api.dto.auth

import com.lowbudgetlcs.domain.auth.models.Session
import com.lowbudgetlcs.domain.auth.models.toSessionId
import com.lowbudgetlcs.domain.user.models.toUserId

fun UserSession.toSession(): Session = Session(id.toSessionId(), userId.toUserId(), expiresAt)

fun Session.toUserSession(): UserSession = UserSession(id.value, userId.value, expiresAt)
