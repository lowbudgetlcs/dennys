package com.lowbudgetlcs.api.routes

import com.lowbudgetlcs.api.dto.riot.PostMatchDto
import com.lowbudgetlcs.domain.event.models.toShortcode
import com.lowbudgetlcs.domain.series.ISeriesService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(Application::class.java)

private val riotCallbackJson = Json { ignoreUnknownKeys = true }

fun Route.riotCallbackRoutes(seriesService: ISeriesService) {
    route("/riot-callback") {
        post {
            val dto = riotCallbackJson.decodeFromString<PostMatchDto>(call.receiveText())
            logger.debug(dto.toString())
            seriesService.refreshFromShortcode(dto.shortCode.toShortcode())
            call.respond(HttpStatusCode.OK)
        }
    }
}
