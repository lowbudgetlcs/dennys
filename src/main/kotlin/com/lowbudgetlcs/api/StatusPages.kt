package com.lowbudgetlcs.api

import com.lowbudgetlcs.api.dto.Error
import com.lowbudgetlcs.domain.auth.UnauthorizedException
import com.lowbudgetlcs.gateways.GatewayException
import com.lowbudgetlcs.repositories.DatabaseException
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.setupStatusPages() {
    install(StatusPages) {
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
        exception<UnauthorizedException> { call, cause ->
            logger.error("⚠️ Unauthorized")
            val code = HttpStatusCode.Unauthorized
            val e = Error(code = code.value, message = cause.message ?: "Not authorized.")
            call.respond(code, e)
        }
        exception<Throwable> { call, cause ->
            logger.error("⚠️ Internal server error: $call", cause)
            val code = HttpStatusCode.InternalServerError
            val e = Error(code = code.value)
            call.respond(code, e)
        }
    }
}
