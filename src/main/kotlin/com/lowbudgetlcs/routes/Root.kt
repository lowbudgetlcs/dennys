package com.lowbudgetlcs.routes

import com.lowbudgetlcs.routes.api.apiRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Application.routes() {
    routing {
        install(StatusPages) {
            exception<RequestValidationException> { call, cause ->
                logger.warn("⚠️ Request failed validation: ${cause.message}")
                call.respond(HttpStatusCode.BadRequest)
            }
            exception<Throwable> { call, cause ->
                logger.error("❌ Uncaught exception on call: $call", cause)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        install(CORS) {
            anyHost()
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
            allowHeader("api_key")
        }
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
