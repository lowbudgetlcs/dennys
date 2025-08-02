package com.lowbudgetlcs.routes.api

import com.lowbudgetlcs.gateways.IRiotAccountGateway
import com.lowbudgetlcs.routes.api.v1.eventRoutesV1
import com.lowbudgetlcs.routes.api.v1.playerRoutesV1
import com.lowbudgetlcs.routes.dto.riot.PostMatchDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.apiRoutes(riotGateway: IRiotAccountGateway) {
    route("/api/v1") {
        route("/riot-callback") {
            post {
                val callback = call.receive<PostMatchDto>()
                logger.info("📩 Received Riot callback: ${Json.encodeToString(callback)}")
                call.respond(HttpStatusCode.OK)
                logger.info("✅ Callback successfully parsed!")
            }
        }
        eventRoutesV1()
        playerRoutesV1(riotGateway = riotGateway)
    }
}