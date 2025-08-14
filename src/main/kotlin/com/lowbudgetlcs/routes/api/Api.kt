package com.lowbudgetlcs.routes.api

import com.lowbudgetlcs.Database
import com.lowbudgetlcs.appConfig
import com.lowbudgetlcs.domain.services.AccountService
import com.lowbudgetlcs.domain.services.EventService
import com.lowbudgetlcs.domain.services.PlayerService
import com.lowbudgetlcs.gateways.RiotAccountGateway
import com.lowbudgetlcs.gateways.TournamentGateway
import com.lowbudgetlcs.repositories.EventGroupRepository
import com.lowbudgetlcs.repositories.EventRepository
import com.lowbudgetlcs.repositories.IAccountRepository
import com.lowbudgetlcs.repositories.IPlayerRepository
import com.lowbudgetlcs.repositories.jooq.JooqAccountRepository
import com.lowbudgetlcs.repositories.jooq.JooqPlayerRepository
import com.lowbudgetlcs.routes.api.v1.account.accountRoutesV1
import com.lowbudgetlcs.routes.api.v1.event.eventRoutesV1
import com.lowbudgetlcs.routes.api.v1.player.playerRoutesV1
import com.lowbudgetlcs.routes.dto.riot.PostMatchDto
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.apiRoutes() {

    val riotHttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
    val riotAccountGateway = RiotAccountGateway(
        client = riotHttpClient, apiKey = appConfig.riot.key
    )

    val accountRepository: IAccountRepository = JooqAccountRepository(Database.dslContext)
    val accountService = AccountService(accountRepository, riotAccountGateway)

    val playerRepository: IPlayerRepository = JooqPlayerRepository(Database.dslContext)
    val playerService = PlayerService(playerRepository, accountRepository)

    val eventRepo = EventRepository(Database.dslContext)
    val eventGroupRepo = EventGroupRepository(Database.dslContext)
    val tournamentGateway = TournamentGateway()
    val eventService = EventService(eventRepo, eventGroupRepo, tournamentGateway)

    route("/api/v1") {
        route("/riot-callback") {
            post {
                val callback = call.receive<PostMatchDto>()
                logger.info("📩 Received Riot callback: ${Json.encodeToString(callback)}")
                call.respond(HttpStatusCode.OK)
                logger.info("✅ Callback successfully parsed!")
            }
        }
        eventRoutesV1(eventService = eventService)
        playerRoutesV1(playerService = playerService)
        accountRoutesV1(accountService = accountService)
    }
}