package com.lowbudgetlcs

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/") {
            get {
                call.respondText("Hello World!")
            }
        }
        healthCheck()
        jsonTest()
    }
}

fun Route.healthCheck() {
    get("/healthcheck") {
        call.respond(HttpStatusCode.OK, "Status: OK")
    }
}

fun Route.jsonTest() = route("/json-test") {
    install(RequestValidation) {
        validate<List<JsonTest>> { requests ->
            val errors = mutableListOf<String>()

            for (req in requests) if (req.count <= 0) errors.add("Count must be greater than 0.")
            if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
        }
    }
    get {
        call.respond(HttpStatusCode.OK, listOf(JsonTest("Title", 1)))
    }
    post {
        try {
            val body = call.receive<List<JsonTest>>()
            call.respond(HttpStatusCode.OK, body)
        } catch (ex: RequestValidationException) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}
