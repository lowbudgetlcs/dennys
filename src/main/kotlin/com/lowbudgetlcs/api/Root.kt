package com.lowbudgetlcs.api

import com.lowbudgetlcs.api.dto.Error
import com.lowbudgetlcs.api.routes.apiRoutes
import com.lowbudgetlcs.gateways.GatewayException
import com.lowbudgetlcs.repositories.DatabaseException
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.resources.Resources
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
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
                val e = Error(code = code.value, message = cause.message ?: "Internal server error")
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
        }
        install(CorrelationIdPlugin)
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
