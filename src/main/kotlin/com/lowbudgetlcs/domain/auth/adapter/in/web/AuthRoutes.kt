package com.lowbudgetlcs.domain.auth.adapter.`in`.web

import com.lowbudgetlcs.domain.auth.adapter.`in`.web.dto.CreateTokenDto
import com.lowbudgetlcs.domain.auth.adapter.`in`.web.dto.UserPrincipal
import com.lowbudgetlcs.domain.auth.adapter.`in`.web.dto.UserSession
import com.lowbudgetlcs.domain.auth.adapter.`in`.web.dto.toNewAccessToken
import com.lowbudgetlcs.domain.auth.adapter.`in`.web.dto.toSession
import com.lowbudgetlcs.domain.auth.adapter.`in`.web.dto.toUserSession
import com.lowbudgetlcs.domain.auth.core.models.types.toUserId
import com.lowbudgetlcs.domain.auth.core.port.IAuthService
import com.lowbudgetlcs.domain.auth.core.port.IUserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    val authService by inject<IAuthService>()
    val userService by inject<IUserService>()
    authenticate("auth-form") {
        post("/login") {
            // We presume that the userId exists in the principal because
            // we have passed the authentication guard to get here.
            val userId = call.principal<UserPrincipal>()!!.userId
            val user = userService.getUser(userId.toUserId())
            val session = authService.createSession(user)
            call.sessions.set<UserSession>(session.toUserSession())
            call.respond(HttpStatusCode.Created)
        }
    }

    authenticate("auth-session") {
        post("/createToken") {
            // We have passed the session auth guard.
            val session = call.sessions.get<UserSession>()!!
            val newTokenData = call.receive<CreateTokenDto>()
            val fresh = authService.createAccessToken(newTokenData.toNewAccessToken(session.userId.toUserId()))
            call.respond(HttpStatusCode.Created, fresh.token)
        }
    }

    authenticate("auth-session") {
        post("/logout") {
            // We presume that the session exists because we have passed the auth
            // guard to get here.
            val userSession = call.sessions.get<UserSession>()!!
            authService.clearSession(userSession.toSession())
            call.sessions.clear<UserSession>()
            call.respond(HttpStatusCode.OK)
        }
    }
}
