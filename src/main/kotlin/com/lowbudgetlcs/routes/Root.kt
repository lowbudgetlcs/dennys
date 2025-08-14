package com.lowbudgetlcs.routes

import com.lowbudgetlcs.domain.models.RepositoryException
import com.lowbudgetlcs.routes.api.apiRoutes
import com.lowbudgetlcs.routes.dto.Error
import com.lowbudgetlcs.routes.dto.accounts.NewRiotAccountDto
import com.lowbudgetlcs.routes.dto.players.AccountLinkRequestDto
import com.lowbudgetlcs.routes.dto.players.NewPlayerDto
import com.lowbudgetlcs.routes.dto.players.PatchPlayerDto
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Application.routes() {
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
            exception<RepositoryException> { call, cause ->
                logger.error("⚠️ Repository Exception: $call", cause)
                val code = HttpStatusCode.InternalServerError
                val e = Error(code = code.value, message = cause.message ?: "Internal server error")
                call.respond(code, e)
            }
            exception<Throwable> { call, cause ->
                logger.error("⚠️ Internal server error: $call", cause)
                val code = HttpStatusCode.InternalServerError
                val e = Error(code = code.value, message = cause.message ?: "Internal server error")
                call.respond(code, e)
            }
        }
        install(CORS) {
            anyHost()
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
            allowHeader("api_key")
        }
        install(Resources)
        route("/") {
            get {
                call.respondText("WHAT THE FUCK IS UP DENNYS????")
            }
        }
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            version = "5.26.1"
        }
        apiRoutes()
    }
}
