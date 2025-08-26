package com.lowbudgetlcs.routes.api

import com.lowbudgetlcs.Database
import com.lowbudgetlcs.appConfig
import com.lowbudgetlcs.domain.services.*
import com.lowbudgetlcs.gateways.RiotAccountGateway
import com.lowbudgetlcs.gateways.RiotTournamentGateway
import com.lowbudgetlcs.repositories.*
import com.lowbudgetlcs.routes.api.v1.account.accountRoutesV1
import com.lowbudgetlcs.routes.api.v1.event.eventRoutesV1
import com.lowbudgetlcs.routes.api.v1.player.playerRoutesV1
import com.lowbudgetlcs.routes.api.v1.series.seriesRoutesV1
import com.lowbudgetlcs.routes.api.v1.team.teamRoutesV1
import com.lowbudgetlcs.routes.dto.InstantSerializer
import com.lowbudgetlcs.routes.dto.riot.PostMatchDto
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.time.Instant

//private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

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
        install(io.ktor.client.plugins.logging.Logging) {
            level = LogLevel.ALL  // log headers, bodies, everything
            logger = Logger.DEFAULT
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
    val riotTournamentGateway = RiotTournamentGateway(
        metadataRepo = metadataRepository,
        client = riotHttpClient,
        apiKey = appConfig.riot.key,
        useStubs = appConfig.riot.usestubs,
    )

    val eventRepository = EventRepository(Database.dslContext)
    val eventService = EventService(eventRepository, riotTournamentGateway, teamRepository, seriesRepository)

    val gameRepository: IGameRepository = GameRepository(Database.dslContext)
    val gameService = GameService(
        gameRepo = gameRepository,
        teamRepo = teamRepository,
        seriesRepo = seriesRepository,
        eventRepo = eventRepository,
        gate = riotTournamentGateway
    )

    route("/api/v1") {
        route("/riot-callback") {
            post {
                val callback = call.receive<PostMatchDto>()
//                logger.info("📩 Received Riot callback: ${Json.encodeToString(callback)}")
                call.respond(HttpStatusCode.OK)
//                logger.info("✅ Callback successfully parsed!")
            }
        }
        eventRoutesV1(eventService = eventService, seriesService = seriesService)
        teamRoutesV1(teamService = teamService)
        playerRoutesV1(playerService = playerService)
        accountRoutesV1(accountService = accountService)
        seriesRoutesV1(
            gameService = gameService
        )
    }
}
