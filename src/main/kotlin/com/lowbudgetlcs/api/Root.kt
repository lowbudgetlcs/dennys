package com.lowbudgetlcs.api

import com.lowbudgetlcs.api.dto.Error
import com.lowbudgetlcs.api.dto.auth.UserPrincipal
import com.lowbudgetlcs.api.dto.auth.UserSession
import com.lowbudgetlcs.api.dto.auth.toSession
import com.lowbudgetlcs.api.routes.apiRoutes
import com.lowbudgetlcs.api.routes.authEndpoints
import com.lowbudgetlcs.config.CookieConfig
import com.lowbudgetlcs.config.CorsConfig
import com.lowbudgetlcs.domain.auth.IAuthService
import com.lowbudgetlcs.domain.auth.UnauthorizedException
import com.lowbudgetlcs.domain.auth.models.toMasked
import com.lowbudgetlcs.domain.user.IUserService
import com.lowbudgetlcs.domain.user.models.toUsername
import com.lowbudgetlcs.gateways.GatewayException
import com.lowbudgetlcs.repositories.DatabaseException
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.bearer
import io.ktor.server.auth.form
import io.ktor.server.auth.session
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.resources.Resources
import io.ktor.server.response.respond
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

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

// Generic endpoint log function.
fun logCall(call: RoutingCall) {
    logger.info("📩 Received ${call.request.httpMethod} on ${call.request.path()}")
}

fun Application.routes() {
    val authService by inject<IAuthService>()
    val userService by inject<IUserService>()
    val cookieConfig by inject<CookieConfig>()
    val corsConfig by inject<CorsConfig>()

    routing {
        install(StatusPages) {
            exception<RequestValidationException> { call, cause ->
                logger.warn("⚠️ Request failed validation: ${cause.reasons.joinToString()}")
                val code = HttpStatusCode.UnprocessableEntity
                val e = Error(code = code.value, message = cause.reasons.joinToString())
                call.respond(code, e)
            }
            exception<BadRequestException> { call, cause ->
                logger.warn("⚠️ Bad request: ${cause.message}")
                val code = HttpStatusCode.BadRequest
                val e = Error(code = code.value, message = "Malformed request body")
                call.respond(code, e)
            }
            exception<JsonConvertException> { call, cause ->
                logger.warn("⚠️ JSON deserialization failed", cause)
                val code = HttpStatusCode.BadRequest
                val e = Error(code = code.value, message = "Invalid JSON format: ${cause.message}")
                call.respond(code, e)
            }
            exception<IllegalArgumentException> { call, cause ->
                logger.warn("⚠️ Invalid input: ${cause.message}")
                val code = HttpStatusCode.UnprocessableEntity
                val e = Error(code = code.value, message = cause.message ?: "Invalid input")
                call.respond(code, e)
            }
            exception<IllegalStateException> { call, cause ->
                logger.warn("⚠️ Conflict: ${cause.message}")
                val code = HttpStatusCode.Conflict
                val e = Error(code = code.value, message = cause.message ?: "Conflict occurred")
                call.respond(code, e)
            }
            exception<NoSuchElementException> { call, cause ->
                logger.warn("⚠️ Not found: ${cause.message}")
                val code = HttpStatusCode.NotFound
                val e = Error(code = code.value, message = cause.message ?: "Not found")
                call.respond(code, e)
            }
            exception<DatabaseException> { call, cause ->
                logger.error("⚠️ Database Exception: $call", cause)
                val code = HttpStatusCode.InternalServerError
                val e = Error(code = code.value, message = cause.message ?: "Internal server error")
                call.respond(code, e)
            }
            exception<GatewayException> { call, cause ->
                logger.error("⚠️ Gateway Exception: $call", cause)
                val code = HttpStatusCode.InternalServerError
                val e = Error(code = code.value, message = cause.message ?: "Internal server error")
                call.respond(code, e)
            }
            exception<UnauthorizedException> { call, cause ->
                logger.error("⚠️ Unauthorized")
                val code = HttpStatusCode.Unauthorized
                val e = Error(code = code.value, message = cause.message ?: "Not authorized.")
                call.respond(code, e)
            }
            exception<Throwable> { call, cause ->
                logger.error("⚠️ Internal server error: $call", cause)
                val code = HttpStatusCode.InternalServerError
                val e = Error(code = code.value)
                call.respond(code, e)
            }
        }
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
        install(Sessions) {
            cookie<UserSession>("user-session") {
                cookie.path = "/"
                // TODO: Put this value in default.properties
                cookie.maxAgeInSeconds = cookieConfig.expiration
                cookie.httpOnly = true
                cookie.sameSite = "none"
                cookie.secure = cookieConfig.secure
            }
        }
        install(AutoHeadResponse)
        route("/") {
            get {
                logCall(call)
                call.respondText("WHAT THE FUCK IS UP DENNYS????")
            }
        }
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            version = "5.26.1"
        }
        authEndpoints(authService, userService)
        apiRoutes()
        // TODO: Add a fancy schmancy healthcheck route.
    }
}
