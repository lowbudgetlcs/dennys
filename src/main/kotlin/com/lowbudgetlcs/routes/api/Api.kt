package com.lowbudgetlcs.routes.api

import com.lowbudgetlcs.Database
import com.lowbudgetlcs.appConfig
import com.lowbudgetlcs.domain.services.AccountService
import com.lowbudgetlcs.domain.services.EventService
import com.lowbudgetlcs.domain.services.PlayerService
import com.lowbudgetlcs.domain.services.SeriesService
import com.lowbudgetlcs.domain.services.TeamService
import com.lowbudgetlcs.gateways.RiotAccountGateway
import com.lowbudgetlcs.gateways.RiotTournamentGateway
import com.lowbudgetlcs.repositories.EventRepository
import com.lowbudgetlcs.repositories.IAccountRepository
import com.lowbudgetlcs.repositories.IPlayerRepository
import com.lowbudgetlcs.repositories.ITeamRepository
import com.lowbudgetlcs.repositories.AccountRepository
import com.lowbudgetlcs.repositories.PlayerRepository
import com.lowbudgetlcs.repositories.TeamRepository
import com.lowbudgetlcs.repositories.MetadataRepository
import com.lowbudgetlcs.repositories.*
import com.lowbudgetlcs.routes.api.v1.account.accountRoutesV1
import com.lowbudgetlcs.routes.api.v1.event.eventRoutesV1
import com.lowbudgetlcs.routes.api.v1.player.playerRoutesV1
import com.lowbudgetlcs.routes.api.v1.team.teamRoutesV1
import com.lowbudgetlcs.routes.dto.InstantSerializer
import com.lowbudgetlcs.routes.dto.riot.PostMatchDto
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.apiRoutes() {
    // Manual dependency wiring. Could be extracted to a DI framework.
    val riotHttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    serializersModule = SerializersModule {
                        contextual(Instant::class, InstantSerializer)
                    }
                })
        }
    }
    val riotAccountGateway = RiotAccountGateway(client = riotHttpClient, apiKey = appConfig.riot.key)

    val accountRepository: IAccountRepository = AccountRepository(Database.dslContext)
    val accountService = AccountService(accountRepository, riotAccountGateway)

    val playerRepository: IPlayerRepository = PlayerRepository(Database.dslContext)
    val playerService = PlayerService(playerRepository, accountRepository)

    val teamRepository: ITeamRepository = TeamRepository(Database.dslContext)
    val teamService = TeamService(teamRepository)

    val seriesRepository: ISeriesRepository = SeriesRepository(Database.dslContext)
    val seriesService = SeriesService(seriesRepository, teamRepository)

    val metadataRepository = MetadataRepository(Database.dslContext)
    val tournamentGateway = RiotTournamentGateway(
        metadataRepo = metadataRepository,
        client = riotHttpClient,
        apiKey = appConfig.riot.key,
        useStubs = appConfig.riot.useStubs,
    )

    val eventRepository = EventRepository(Database.dslContext)
    val eventService = EventService(eventRepository, tournamentGateway, teamRepository, seriesRepository)

    route("/api/v1") {
        route("/riot-callback") {
            post {
                val callback = call.receive<PostMatchDto>()
                logger.info("📩 Received Riot callback: ${Json.encodeToString(callback)}")
                call.respond(HttpStatusCode.OK)
                logger.info("✅ Callback successfully parsed!")
            }
        }
        eventRoutesV1(eventService = eventService, seriesService = seriesService)
        teamRoutesV1(teamService = teamService)
        playerRoutesV1(playerService = playerService)
        accountRoutesV1(accountService = accountService)
    }
}
