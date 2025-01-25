package com.lowbudgetlcs.routes.api.v1.jsontest

import io.ktor.http.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.jsonTestRoutes() {
    install(RequestValidation) {
        validate<List<TestJson>> { requests ->
            val errors = mutableListOf<String>()
            for (req in requests) if (req.count <= 0) errors.add("Count must be greater than 0.")
            if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
        }
    }
    route("/json-test") {
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
