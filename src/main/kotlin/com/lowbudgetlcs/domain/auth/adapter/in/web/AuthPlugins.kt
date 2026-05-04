package com.lowbudgetlcs.domain.auth.adapter.`in`.web

import com.lowbudgetlcs.domain.auth.core.UnauthorizedException
import com.lowbudgetlcs.domain.auth.adapter.`in`.web.dto.UserPrincipal
import com.lowbudgetlcs.domain.auth.adapter.`in`.web.dto.UserSession
import com.lowbudgetlcs.domain.auth.adapter.`in`.web.dto.toSession
import com.lowbudgetlcs.domain.auth.core.models.types.toUsername
import com.lowbudgetlcs.domain.auth.core.port.IAuthService
import com.lowbudgetlcs.toMasked
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.bearer
import io.ktor.server.auth.form
import io.ktor.server.auth.session
import io.ktor.server.response.respond
import org.koin.ktor.ext.inject

fun Application.setupAuth() {
    val authService by inject<IAuthService>()
    install(Authentication) {
        form("auth-form") {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                val user = authService.authenticate(credentials.name.toUsername(), credentials.password.toMasked())
                UserPrincipal(user.id.value, user.username.value, user.roles)
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials passed.")
            }
        }
        session<UserSession>("auth-session") {
            validate { session ->
                authService.validateSession(session.toSession())
                session
            }
            challenge {
                throw UnauthorizedException("Invalid session.")
            }
        }
        bearer("auth-token") {
            realm = "/"
            authenticate { bearer ->
                val user = authService.authenticate(bearer.token)
                UserPrincipal(user.id.value, user.username.value, user.roles)
            }
        }
    }
}
