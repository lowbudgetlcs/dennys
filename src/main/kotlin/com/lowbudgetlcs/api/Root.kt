package com.lowbudgetlcs.api

import com.lowbudgetlcs.api.dto.auth.UserSession
import com.lowbudgetlcs.api.routes.apiRoutes
import com.lowbudgetlcs.api.routes.authEndpoints
import com.lowbudgetlcs.config.CookieConfig
import com.lowbudgetlcs.domain.auth.IAuthService
import com.lowbudgetlcs.domain.user.IUserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger(Application::class.java)

// Generic endpoint log function.
fun logCall(call: RoutingCall) {
    logger.info("📩 Received ${call.request.httpMethod} on ${call.request.path()}")
}

fun Application.routes() {
    val authService by inject<IAuthService>()
    val userService by inject<IUserService>()
    val cookieConfig by inject<CookieConfig>()

    routing {
        setupStatusPages()
        setupAuth()
        install(CORS) {
            anyHost()
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
            allowHeader("api_key")
            allowMethod(HttpMethod.Patch)
            allowMethod(HttpMethod.Delete)
            allowCredentials = true
        }
        install(CorrelationIdPlugin)
        install(Resources)
        install(Sessions) {
            cookie<UserSession>("user-session") {
                cookie.path = "/"
                cookie.maxAgeInSeconds = cookieConfig.expiration
                cookie.httpOnly = true
                cookie.sameSite = "strict"
                cookie.secure = cookieConfig.secure
            }
        }
        install(AutoHeadResponse)
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            version = "5.26.1"
        }
        authEndpoints(authService, userService)
        apiRoutes()
        route("/health") {
            get {
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
