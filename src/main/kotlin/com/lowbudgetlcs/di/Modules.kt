package com.lowbudgetlcs.di

import com.lowbudgetlcs.api.dto.InstantSerializer
import com.lowbudgetlcs.config.AppConfig
import com.lowbudgetlcs.config.DatabaseConfig
import com.lowbudgetlcs.config.RiotConfig
import com.lowbudgetlcs.config.appConfig
import com.lowbudgetlcs.createDslContext
import com.lowbudgetlcs.domain.services.account.AccountService
import com.lowbudgetlcs.domain.services.account.IAccountService
import com.lowbudgetlcs.domain.services.event.EventService
import com.lowbudgetlcs.domain.services.event.IEventService
import com.lowbudgetlcs.domain.services.event.group.EventGroupService
import com.lowbudgetlcs.domain.services.event.group.IEventGroupService
import com.lowbudgetlcs.domain.services.game.GameService
import com.lowbudgetlcs.domain.services.game.IGameService
import com.lowbudgetlcs.domain.services.player.IPlayerService
import com.lowbudgetlcs.domain.services.player.PlayerService
import com.lowbudgetlcs.domain.services.series.ISeriesService
import com.lowbudgetlcs.domain.services.series.SeriesService
import com.lowbudgetlcs.domain.services.team.ITeamService
import com.lowbudgetlcs.domain.services.team.TeamService
import com.lowbudgetlcs.gateways.riot.account.IRiotAccountGateway
import com.lowbudgetlcs.gateways.riot.account.RiotAccountGateway
import com.lowbudgetlcs.gateways.riot.tournament.IRiotTournamentGateway
import com.lowbudgetlcs.gateways.riot.tournament.RiotTournamentGateway
import com.lowbudgetlcs.repositories.account.AccountRepository
import com.lowbudgetlcs.repositories.account.IAccountRepository
import com.lowbudgetlcs.repositories.event.EventRepository
import com.lowbudgetlcs.repositories.event.IEventRepository
import com.lowbudgetlcs.repositories.event.group.EventGroupRepository
import com.lowbudgetlcs.repositories.event.group.IEventGroupRepository
import com.lowbudgetlcs.repositories.game.GameRepository
import com.lowbudgetlcs.repositories.game.IGameRepository
import com.lowbudgetlcs.repositories.player.IPlayerRepository
import com.lowbudgetlcs.repositories.player.PlayerRepository
import com.lowbudgetlcs.repositories.series.ISeriesRepository
import com.lowbudgetlcs.repositories.series.SeriesRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository
import com.lowbudgetlcs.repositories.team.TeamRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.jooq.DSLContext
import org.koin.dsl.module
import java.time.Instant

val configModule = module {
    single<RiotConfig> { appConfig.riot }
    single<DatabaseConfig> { appConfig.database }
    single<AppConfig> { appConfig }
}

val httpClientModule = module {
    single {
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
    }
}

val databaseModule = module {
    single<DSLContext> { createDslContext(get()) }
}

val serviceModule = module {
    single<IGameService> { GameService(get(), get(), get(), get(), get()) }
    single<ITeamService> { TeamService(get()) }
    single<IEventService> { EventService(get(), get(), get(), get()) }
    single<IEventGroupService> { EventGroupService(get(), get()) }
    single<IPlayerService> { PlayerService(get(), get()) }
    single<ISeriesService> { SeriesService(get(), get()) }
    single<IAccountService> { AccountService(get(), get()) }
}

val dataModule = module {
    single<IGameRepository> { GameRepository(get()) }
    single<ITeamRepository> { TeamRepository(get()) }
    single<IEventRepository> { EventRepository(get()) }
    single<IEventGroupRepository> { EventGroupRepository(get()) }
    single<IPlayerRepository> { PlayerRepository(get()) }
    single<ISeriesRepository> { SeriesRepository(get()) }
    single<IAccountRepository> { AccountRepository(get()) }
}

val gatewayModule = module {
    single<IRiotAccountGateway> {
        RiotAccountGateway(
            client = get(),
            apiKey = get<RiotConfig>().key
        )
    }
    single<IRiotTournamentGateway> {
        RiotTournamentGateway(
            client = get(),
            apiKey = get<RiotConfig>().key,
            useStubs = get<RiotConfig>().usestubs,
            providerId = get<RiotConfig>().providerid,
        )
    }
}
