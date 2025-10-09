package com.lowbudgetlcs.api.routes

import com.lowbudgetlcs.Database
import com.lowbudgetlcs.api.dto.InstantSerializer
import com.lowbudgetlcs.api.dto.riot.PostMatchDto
import com.lowbudgetlcs.api.routes.auth.authRoutes
import com.lowbudgetlcs.api.routes.v1.account.accountRoutesV1
import com.lowbudgetlcs.api.routes.v1.event.eventRoutesV1
import com.lowbudgetlcs.api.routes.v1.event.group.eventGroupRoutesV1
import com.lowbudgetlcs.api.routes.v1.player.playerRoutesV1
import com.lowbudgetlcs.api.routes.v1.series.seriesRoutesV1
import com.lowbudgetlcs.api.routes.v1.team.teamRoutesV1
import com.lowbudgetlcs.config.appConfig
import com.lowbudgetlcs.domain.services.account.AccountService
import com.lowbudgetlcs.domain.services.event.EventService
import com.lowbudgetlcs.domain.services.event.group.EventGroupService
import com.lowbudgetlcs.domain.services.game.GameService
import com.lowbudgetlcs.domain.services.player.PlayerService
import com.lowbudgetlcs.domain.services.series.SeriesService
import com.lowbudgetlcs.domain.services.team.TeamService
import com.lowbudgetlcs.domain.services.user.UserService
import com.lowbudgetlcs.gateways.riot.account.RiotAccountGateway
import com.lowbudgetlcs.gateways.riot.tournament.RiotTournamentGateway
import com.lowbudgetlcs.repositories.account.AccountRepository
import com.lowbudgetlcs.repositories.account.IAccountRepository
import com.lowbudgetlcs.repositories.event.EventRepository
import com.lowbudgetlcs.repositories.event.group.EventGroupRepository
import com.lowbudgetlcs.repositories.game.GameRepository
import com.lowbudgetlcs.repositories.game.IGameRepository
import com.lowbudgetlcs.repositories.metadata.MetadataRepository
import com.lowbudgetlcs.repositories.player.IPlayerRepository
import com.lowbudgetlcs.repositories.player.PlayerRepository
import com.lowbudgetlcs.repositories.series.ISeriesRepository
import com.lowbudgetlcs.repositories.series.SeriesRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository
import com.lowbudgetlcs.repositories.team.TeamRepository
import com.lowbudgetlcs.repositories.user.UserRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.apiRoutes() {
    // Manual dependency wiring. Could be extracted to a DI framework.
    val riotHttpClient =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        encodeDefaults = true
                        serializersModule =
                            SerializersModule {
                                contextual(Instant::class, InstantSerializer)
                            }
                    },
                )
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
    val riotTournamentGateway =
        RiotTournamentGateway(
            metadataRepo = metadataRepository,
            client = riotHttpClient,
            apiKey = appConfig.riot.key,
            useStubs = appConfig.riot.usestubs,
        )

    val eventRepository = EventRepository(Database.dslContext)
    val eventService = EventService(eventRepository, riotTournamentGateway, teamRepository, seriesRepository)

    val gameRepository: IGameRepository = GameRepository(Database.dslContext)
    val gameService =
        GameService(
            gameRepo = gameRepository,
            teamRepo = teamRepository,
            seriesRepo = seriesRepository,
            eventRepo = eventRepository,
            gate = riotTournamentGateway,
        )

    val eventGroupRepository = EventGroupRepository(Database.dslContext)
    val eventGroupService = EventGroupService(eventGroupRepository, eventRepository)

    val userRepository = UserRepository(Database.dslContext)
    val userService = UserService(userRepository)

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
        seriesRoutesV1(
            gameService = gameService,
        )
        eventGroupRoutesV1(eventGroupService)
    }
    route("/api") {
        authRoutes(userService = userService)
    }
}
