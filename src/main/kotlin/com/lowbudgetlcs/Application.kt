package com.lowbudgetlcs

import com.lowbudgetlcs.routes.routes
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.configureRouting() {
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
    install(ContentNegotiation) {
        json()
    }
    routes()
}

fun Application.module() {
    logger.info("🔧 Performing opening duties...")
    configureRouting()
    logger.info("🍽️ Denny's is open! Ready to serve requests. 🚀")
}

