package com.lowbudgetlcs.api

import com.lowbudgetlcs.api.dto.auth.UserSession
import com.lowbudgetlcs.api.routes.apiRoutes
import com.lowbudgetlcs.api.routes.authEndpoints
import com.lowbudgetlcs.config.CookieConfig
import com.lowbudgetlcs.domain.auth.IAuthService
import com.lowbudgetlcs.domain.user.IUserService
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.http.content.vue
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.resources.Resources
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.sameSite
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
        route("/") {
            singlePageApplication {
                vue("/frontend")
            }
        }
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            version = "5.26.1"
        }
        authEndpoints(authService, userService)
        apiRoutes()
        route("/health") {
            get {
                call.respondText("OK")
            }
        }
    }
}
