package com.lowbudgetlcs.routes.jsontest

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.jsonTestRoutes() {
    routing {
        route("/json-test") {
            install(RequestValidation) {
                validate<List<TestJson>> { requests ->
                    val errors = mutableListOf<String>()

                    for (req in requests) if (req.count <= 0) errors.add("Count must be greater than 0.")
                    if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(
                        errors
                    )
                }
            }
            get {
                val body: List<TestJson> = listOf(TestJson("Title", 1))
                call.respond(HttpStatusCode.OK, body)
            }
            post {
                val body = call.receive<List<TestJson>>()
                call.respond(HttpStatusCode.OK, body)
            }
        }
    }
}
