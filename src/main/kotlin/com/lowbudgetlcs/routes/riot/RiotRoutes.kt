package com.lowbudgetlcs.routes.riot

import com.lowbudgetlcs.Database
import com.lowbudgetlcs.RiotApiClient
import com.lowbudgetlcs.models.PostMatchCallback
import com.lowbudgetlcs.repositories.*
import com.lowbudgetlcs.util.RateLimiter
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Application.riotRoutes() {
    logger.info("🚀 Initializing Riot routes...")
    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            logger.warn("⚠️ Request failed validation: ${cause.message}")
            call.respond(HttpStatusCode.BadRequest)
        }
        exception<Throwable> { call, cause ->
            logger.error("❌ Uncaught exception on call: $call", cause)
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
    routing {
        route("/riot-callback") {
            post {
                val callback = call.receive<PostMatchCallback>()
                logger.info("📩 Received Riot callback: ${Json.encodeToString(callback)}")
                // Emit callback onto all registered queues.
                val database = Database().db
                TournamentService(
                    gamesRepository = DatabaseGameRepository(database),
                    seriesRepository = DatabaseSeriesRepository(database),
                    playersRepository = DatabasePlayerRepository(database),
                    matchRepository = RiotMatchRepository(RiotApiClient(), RateLimiter()),
                    database
                ).process(callback)
                StatService(
                    gamesRepository = DatabaseGameRepository(database),
                    playersRepository = DatabasePlayerRepository(database),
                    teamsRepository = DatabaseTeamRepository(database),
                    matchRepository = RiotMatchRepository(RiotApiClient(), RateLimiter())
                ).process(callback)
                logger.info("✅ Callback successfully processed!")
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
