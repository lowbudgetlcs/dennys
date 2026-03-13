package com.lowbudgetlcs.api

import com.lowbudgetlcs.api.dto.auth.UserPrincipal
import com.lowbudgetlcs.api.dto.auth.UserSession
import com.lowbudgetlcs.api.dto.auth.toSession
import com.lowbudgetlcs.domain.auth.IAuthService
import com.lowbudgetlcs.domain.auth.UnauthorizedException
import com.lowbudgetlcs.domain.auth.models.toMasked
import com.lowbudgetlcs.domain.user.models.toUsername
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
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
