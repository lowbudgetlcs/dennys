package com.lowbudgetlcs

import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(RequestValidation) {
        validate<String> { bodyText ->
            if (!bodyText.startsWith("Hello")) ValidationResult.Invalid("Body text should start with 'Hello'")
            else ValidationResult.Valid
        }
    }
    install(AutoHeadResponse)
    routing {
        route("/") {
            get {
                call.respondText("Hello World!")
            }
            post {
                try {
                    val body = call.receive<String>()
                    print(body)
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: JsonConvertException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
        get("/healthcheck") {
            val text = "Status: OK"
            call.respond(text)
        }
        get("/test") {
            val text = "<h1>I am bad at Nilah !!</h1>"
            val type = ContentType.parse("text/html")
            call.respondText(text, type)
        }
    }
}
