package com.lowbudgetlcs.routes.riot

import com.lowbudgetlcs.bridges.RabbitMQBridge
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val logger = KtorSimpleLogger("com.lowbudgetlcs.routes.riot.Riot")

fun Application.riotRoutes() {
    // List of messageqs to emit callbacks onto. This is basically 'registering service workers',
    // but really shitty.
    val messageqs = mutableListOf<RabbitMQBridge>()
    messageqs.add(RabbitMQBridge("CALLBACK"))
    messageqs.add(RabbitMQBridge("STATS"))
    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            logger.warn("Request Failed validation: $cause")
            call.respond(HttpStatusCode.BadRequest)
        }
        exception<Throwable> { call, cause ->
            logger.warn("Uncaught throwable: $cause")
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
    routing {
        route("/riot") {
            route("/callback") {
                post {
                    val callback = call.receive<RiotCallback>()
                    // Emit callback onto all registered queues.
                    for (queue in messageqs) queue.emit(Json.encodeToString(callback))
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}
