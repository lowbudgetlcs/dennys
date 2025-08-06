package com.lowbudgetlcs.routes

import com.lowbudgetlcs.routes.api.apiRoutes
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
                call.respond(HttpStatusCode.UnprocessableEntity, cause.reasons.joinToString())
            }
            exception<BadRequestException> { call, cause ->
                logger.warn("⚠️ Bad request: ${cause.message}")
                call.respond(HttpStatusCode.BadRequest, "Malformed request body")
            }
            exception<JsonConvertException> { call, cause ->
                logger.warn("⚠️ JSON deserialization failed", cause)
                call.respond(HttpStatusCode.BadRequest, "Invalid JSON format: ${cause.message}")
            }
            exception<IllegalArgumentException> { call, cause ->
                logger.warn("⚠️ Invalid input: ${cause.message}")
                call.respond(HttpStatusCode.UnprocessableEntity, cause.message ?: "Invalid input")
            }
            exception<IllegalStateException> { call, cause ->
                logger.warn("⚠️ Conflict: ${cause.message}")
                call.respond(HttpStatusCode.Conflict, cause.message ?: "Conflict occurred")
            }
            exception<NoSuchElementException> { call, cause ->
                logger.warn("⚠️ Not found: ${cause.message}")
                call.respond(HttpStatusCode.NotFound, cause.message ?: "Not found")
            }
            exception<Throwable> { call, cause ->
                logger.error("❌ Uncaught exception on call: $call", cause)
                call.respond(HttpStatusCode.InternalServerError, "Internal server error")
            }
        }
        install(RequestValidation) {
            validate<NewRiotAccountDto> { dto ->
                when {
                    dto.riotPuuid.isBlank() -> ValidationResult.Invalid("PUUID cannot be blank")
                    else -> ValidationResult.Valid
                }
            }

            validate<NewPlayerDto> { dto ->
                when {
                    dto.name.isBlank() -> ValidationResult.Invalid("Player name cannot be blank")
                    else -> ValidationResult.Valid
                }
            }

            validate<PatchPlayerDto> { dto ->
                when {
                    dto.name.isBlank() -> ValidationResult.Invalid("New name cannot be blank")
                    else -> ValidationResult.Valid
                }
            }

            validate<AccountLinkRequestDto> { dto ->
                when {
                    dto.accountId <= 0 -> ValidationResult.Invalid("Account ID must be greater than 0")
                    else -> ValidationResult.Valid
                }
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
