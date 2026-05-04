package com.lowbudgetlcs.api

import com.lowbudgetlcs.api.dto.riot.PostMatchDto
import com.lowbudgetlcs.api.plugins.setupCors
import com.lowbudgetlcs.api.plugins.setupLogging
import com.lowbudgetlcs.api.plugins.setupSessions
import com.lowbudgetlcs.api.plugins.setupStatusPages
import com.lowbudgetlcs.api.routes.apiRoutes
import com.lowbudgetlcs.api.routes.healthRoutes
import com.lowbudgetlcs.api.routes.uiRoutes
import com.lowbudgetlcs.domain.auth.adapter.`in`.web.authRoutes
import com.lowbudgetlcs.domain.auth.adapter.`in`.web.setupAuth
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.request.receive
import io.ktor.server.resources.Resources
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

private val logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
fun Application.routes() {
    routing {
        setupLogging()
        setupStatusPages()
        setupAuth()
        setupCors()
        setupSessions()
        install(Resources)
        install(AutoHeadResponse)
        uiRoutes()
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            version = "5.26.1"
        }
        healthRoutes()
        authRoutes()
        apiRoutes()
        route("/riot-callback") {
            post {
                val dto = call.receive<PostMatchDto>()
                logger.debug(dto.toString())
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
