package com.lowbudgetlcs.api

import com.lowbudgetlcs.api.auth.UserPrincipal
import com.lowbudgetlcs.api.dto.Error
import com.lowbudgetlcs.api.routes.apiRoutes
import com.lowbudgetlcs.api.routes.auth.authEndpoints
import com.lowbudgetlcs.domain.models.auth.UserSession
import com.lowbudgetlcs.domain.services.auth.AuthService
import com.lowbudgetlcs.domain.services.user.UserService
import com.lowbudgetlcs.gateways.GatewayException
import com.lowbudgetlcs.repositories.DatabaseException
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun logCall(call: RoutingCall) {
    logger.info("📩 Received ${call.request.httpMethod} on ${call.request.path()}")
}

fun Application.routes() {
    val authService = AuthService()
    val userService = UserService()

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
                    UserPrincipal(authService.authenticate(credentials.name, credentials.password).id)
                }
                challenge {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid credentials passed.")
                }

            }
            session<UserSession>("auth-session") {
                validate { session ->
                    if (authService.validateSession(session)) {
                        session
                    } else {
                        null
                    }
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
                cookie.maxAgeInSeconds = 60*60*3
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
