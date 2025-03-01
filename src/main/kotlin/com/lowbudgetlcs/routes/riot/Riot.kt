package com.lowbudgetlcs.routes.riot

import com.lowbudgetlcs.bridges.RabbitMQBridge
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Application.riotRoutes() {
    logger.info("🚀 Initializing Riot routes...")

    // List of messageqs to emit callbacks onto. This is basically 'registering service workers',
    // but really shitty.
    val messageqs = mutableListOf<RabbitMQBridge>()
    messageqs.add(RabbitMQBridge("CALLBACK"))
    messageqs.add(RabbitMQBridge("STATS"))

    logger.info("📡 Registered queues: $messageqs")

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
    routing {
        route("/riot") {
            logger.info("📂 Setting up Riot API routes...")
            route("/callback") {
                post {
                    val callback = call.receive<RiotCallback>()
                    logger.info("📩 Received Riot callback: ${Json.encodeToString(callback)}")
                    // Emit callback onto all registered queues.
                    for (queue in messageqs) queue.emit(Json.encodeToString(callback))
                    logger.info("✅ Callback successfully processed!")
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}
