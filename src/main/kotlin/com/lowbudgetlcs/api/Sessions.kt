package com.lowbudgetlcs.api

import com.lowbudgetlcs.api.routes.auth.dto.UserSession
import com.lowbudgetlcs.config.CookieConfig
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.sameSite
import org.koin.ktor.ext.inject

fun Application.setupSessions() {
    val cookieConfig by inject<CookieConfig>()
    install(Sessions) {
        cookie<UserSession>("user-session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = cookieConfig.expiration
            cookie.httpOnly = true
            cookie.sameSite = "strict"
            cookie.secure = cookieConfig.secure
        }
    }
}
