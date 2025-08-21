package com.lowbudgetlcs.routes.api

import com.lowbudgetlcs.Database
import com.lowbudgetlcs.appConfig
import com.lowbudgetlcs.domain.services.AccountService
import com.lowbudgetlcs.domain.services.PlayerService
import com.lowbudgetlcs.domain.services.TeamService
import com.lowbudgetlcs.gateways.RiotAccountGateway
import com.lowbudgetlcs.repositories.IAccountRepository
import com.lowbudgetlcs.repositories.IPlayerRepository
import com.lowbudgetlcs.repositories.ITeamRepository
import com.lowbudgetlcs.repositories.jooq.AccountRepository
import com.lowbudgetlcs.repositories.jooq.PlayerRepository
import com.lowbudgetlcs.repositories.jooq.TeamRepository
import com.lowbudgetlcs.routes.api.v1.account.accountRoutesV1
import com.lowbudgetlcs.routes.api.v1.eventRoutesV1
import com.lowbudgetlcs.routes.api.v1.player.playerRoutesV1
import com.lowbudgetlcs.routes.api.v1.team.teamRoutesV1
import com.lowbudgetlcs.routes.dto.riot.PostMatchDto
import io.ktor.client.*
import io.ktor.client.engine.cio.*
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
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
    val riotAccountGateway = RiotAccountGateway(
        client = riotHttpClient,
        apiKey = appConfig.riot.key
    )

    val accountRepository: IAccountRepository = AccountRepository(Database.dslContext)
    val accountService = AccountService(accountRepository, riotAccountGateway)

    val playerRepository : IPlayerRepository = PlayerRepository(Database.dslContext)
    val playerService = PlayerService(playerRepository, accountRepository)

    val teamRepository : ITeamRepository = TeamRepository(Database.dslContext)
    val teamService = TeamService(teamRepository)

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
        playerRoutesV1(
            playerService = playerService
        )
        accountRoutesV1(
            accountService = accountService
        )
        teamRoutesV1(
            teamService = teamService
        )
    }
}