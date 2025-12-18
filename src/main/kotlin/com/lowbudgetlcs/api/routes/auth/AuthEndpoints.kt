package com.lowbudgetlcs.api.routes.auth

import com.lowbudgetlcs.api.auth.UserPrincipal
import com.lowbudgetlcs.api.logCall
import com.lowbudgetlcs.api.setCidContext
import com.lowbudgetlcs.domain.services.auth.IAuthService
import com.lowbudgetlcs.domain.services.user.IUserService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.authEndpoints(authService: IAuthService, userService: IUserService) {
    authenticate("auth-form") {
        post("/login") {
            call.setCidContext {
                logCall(call)
                // We presume that the userId exists in the principal because
                // we have passed the authentication guard.
                val userId = call.principal<UserPrincipal>()!!.userId
                val user = userService.getUser(userId)
                val session = authService.createSession(user)
                call.sessions.set(session)
                // DO NOT LOG USER CREDENTIALS!!!
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
