package com.lowbudgetlcs.api

import com.lowbudgetlcs.api.routes.apiRoutes
import com.lowbudgetlcs.api.routes.authRoutes
import com.lowbudgetlcs.api.routes.riotCallbackRoutes
import com.lowbudgetlcs.domain.series.ISeriesService
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.http.content.vue
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.resources.Resources
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Application.routes() {
    val seriesService by inject<ISeriesService>()

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
        }
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            version = "5.26.1"
        }
        route("/health") {
            get {
                call.respondText("OK")
            }
        }
        riotCallbackRoutes(seriesService)
        authRoutes()
        apiRoutes()
    }
}
