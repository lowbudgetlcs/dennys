package com.lowbudgetlcs.api

import com.lowbudgetlcs.api.dto.riot.PostMatchDto
import com.lowbudgetlcs.api.routes.apiRoutes
import com.lowbudgetlcs.api.routes.authRoutes
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.openapi.OpenApiInfo
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.http.content.vue
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.request.receive
import io.ktor.server.resources.Resources
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.openapi.OpenApiDocSource
import io.ktor.server.routing.openapi.hide
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.utils.io.ExperimentalKtorApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger(Application::class.java)

@OptIn(ExperimentalKtorApi::class)
fun Application.routes() {

    routing {
        setupLogging()
        setupStatusPages()
        setupAuth()
        setupCors()
        setupSessions()
        install(Resources)
        install(AutoHeadResponse)
        route("/") {
            singlePageApplication {
                vue("/frontend")
            }
        }.hide()
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            version = "5.26.1"
        }
        swaggerUI("/swaggerCompiled") {
            info = OpenApiInfo(
                "Dennys", "1.0.0"
            )
            source = OpenApiDocSource.Routing(
                contentType = ContentType.Application.Json,
            )
        }
        route("/health") {
            get {
                call.respondText("OK")
            }
        }
        route("/riot-callback") {
            post {
                val dto = call.receive<PostMatchDto>()
                logger.debug(dto.toString())
                call.respond(HttpStatusCode.OK)
            }
        }.hide()
        authRoutes()
        apiRoutes()
    }
}
