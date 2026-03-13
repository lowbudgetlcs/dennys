package com.lowbudgetlcs.api.routes

import com.lowbudgetlcs.api.dto.CreateTokenDto
import com.lowbudgetlcs.api.dto.auth.UserPrincipal
import com.lowbudgetlcs.api.dto.auth.UserSession
import com.lowbudgetlcs.api.dto.auth.toSession
import com.lowbudgetlcs.api.dto.auth.toUserSession
import com.lowbudgetlcs.api.dto.toNewAccessToken
import com.lowbudgetlcs.api.logCall
import com.lowbudgetlcs.api.setCidContext
import com.lowbudgetlcs.domain.auth.IAuthService
import com.lowbudgetlcs.domain.user.IUserService
import com.lowbudgetlcs.domain.user.models.toUserId
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
                call.sessions.set<UserSession>(session.toUserSession())
                call.respond(HttpStatusCode.Created)
            }
        }
    }

    authenticate("auth-session") {
        post("/createToken") {
            call.setCidContext {
                logCall(call)
                // We have passed the session auth guard.
                val session = call.sessions.get<UserSession>()!!
                val newTokenData = call.receive<CreateTokenDto>()
                val fresh = authService.createAccessToken(newTokenData.toNewAccessToken(session.userId.toUserId()))
                call.respond(HttpStatusCode.Created, fresh.token)
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
