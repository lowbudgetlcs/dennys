package com.lowbudgetlcs.api.routes

import com.lowbudgetlcs.api.auth.UserPrincipal
import com.lowbudgetlcs.api.auth.UserSession
import com.lowbudgetlcs.api.auth.toSession
import com.lowbudgetlcs.api.logCall
import com.lowbudgetlcs.api.setCidContext
import com.lowbudgetlcs.domain.models.auth.toUserId
import com.lowbudgetlcs.domain.services.auth.IAuthService
import com.lowbudgetlcs.domain.services.user.IUserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set

fun Route.authEndpoints(
    authService: IAuthService,
    userService: IUserService,
) {
    authenticate("auth-form") {
        post("/login") {
            call.setCidContext {
                logCall(call)
                // We presume that the userId exists in the principal because
                // we have passed the authentication guard to get here.
                val userId = call.principal<UserPrincipal>()!!.userId
                val user = userService.getUser(userId.toUserId())
                val session = authService.createSession(user)
                call.sessions.set(session)
                call.respond(HttpStatusCode.OK)
            }
        }
    }

    authenticate("auth-session") {
        post("/logout") {
            call.setCidContext {
                logCall(call)
                // We presume that the session exists because we have passed the auth
                // guard to get here.
                val userSession = call.sessions.get<UserSession>()!!
                authService.clearSession(userSession.toSession())
                call.sessions.clear<UserSession>()
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
