package com.lowbudgetlcs.api

import com.lowbudgetlcs.Database
import com.lowbudgetlcs.api.auth.PasswordHasher
import com.lowbudgetlcs.api.auth.UserPrincipal
import com.lowbudgetlcs.api.auth.UserSession
import com.lowbudgetlcs.api.auth.toSession
import com.lowbudgetlcs.api.dto.Error
import com.lowbudgetlcs.api.routes.apiRoutes
import com.lowbudgetlcs.api.routes.authEndpoints
import com.lowbudgetlcs.domain.services.auth.AuthService
import com.lowbudgetlcs.domain.services.auth.UnauthorizedException
import com.lowbudgetlcs.domain.services.user.UserService
import com.lowbudgetlcs.gateways.GatewayException
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.session.SessionRepository
import com.lowbudgetlcs.repositories.user.UserRepostitory
import com.sksamuel.hoplite.Masked
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.form
import io.ktor.server.auth.session
import io.ktor.server.plugins.BadRequestException
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
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

// Generic endpoint log function.
fun logCall(call: RoutingCall) {
    logger.info("📩 Received ${call.request.httpMethod} on ${call.request.path()}")
}

fun Application.routes() {
    val userRepository = UserRepostitory(Database.dslContext)
    val sessionRepository = SessionRepository(Database.dslContext)
    // The hasher initialization takes nearly 20 seconds...
    val hasher = PasswordHasher()
    val authService = AuthService(sessionRepository, userRepository, hasher)
    val userService = UserService(userRepository)

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
            allowHeader("X-Dennys-Token")
            allowHeader("api_key")
            allowMethod(HttpMethod.Patch)
            allowMethod(HttpMethod.Delete)
        }
        install(CorrelationIdPlugin)
        install(Resources)
        install(Authentication) {
            form("auth-form") {
                userParamName = "username"
                passwordParamName = "password"
                validate { credentials ->
                    val user = authService.authenticate(credentials.name, Masked(credentials.password))
                    UserPrincipal(user.id.value, user.username, user.roles)
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
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }
        install(Sessions) {
            cookie<UserSession>("user-session") {
                cookie.path = "/"
                // TODO: Put this value in default.properties
                cookie.maxAgeInSeconds = 60 * 60 * 3
                cookie.httpOnly = true
                cookie.sameSite = "strict"
                cookie.secure = true
            }
        }
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
    }
}
