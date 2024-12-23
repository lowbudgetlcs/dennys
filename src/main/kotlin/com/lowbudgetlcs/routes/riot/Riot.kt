package com.lowbudgetlcs.routes.riot

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*

private val logger = KtorSimpleLogger("com.lowbudgetlcs.routes.riot.Riot")

fun Application.riotRoutes() {
    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            logger.warn("Request Failed validation: $cause")
            call.respond(HttpStatusCode.BadRequest)
        }
        exception<Throwable> { call, cause ->
            logger.warn("Uncaught throwable: $cause")
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        route("/riot") {
            route("/callback") {
                post {
                    val callback = call.receive<RiotCallback>()
                    logger.info(callback.toString())
                    call.respond(HttpStatusCode.OK, callback)
                }
            }
        }
    }
}
