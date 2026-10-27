package com.lowbudgetlcs

import com.lowbudgetlcs.api.routes
import com.lowbudgetlcs.config.RiotConfig
import com.lowbudgetlcs.config.modules.configModule
import com.lowbudgetlcs.config.modules.databaseModule
import com.lowbudgetlcs.config.modules.gatewayModule
import com.lowbudgetlcs.config.modules.hashingModule
import com.lowbudgetlcs.config.modules.httpClientModule
import com.lowbudgetlcs.config.modules.repositoryModule
import com.lowbudgetlcs.config.modules.serviceModule
import com.lowbudgetlcs.domain.auth.IAuthService
import com.lowbudgetlcs.domain.series.ISeriesService
import com.lowbudgetlcs.serializers.InstantSerializer
import com.lowbudgetlcs.serializers.UUIDSerializer
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

private val SESSION_CLEANUP_DELAY = 360.seconds

private val RIOT_POLL_DELAY = 5.minutes

/** Ticks between polls that include stale codes, so those are asked about hourly. */
private const val STALE_POLL_EVERY = 12

fun main(args: Array<String>) =
    io.ktor.server.netty.EngineMain
        .main(args)

fun Application.startSessionCleanup() =
    CoroutineScope(Dispatchers.Default).launch {
        logger.debug("Starting session cleanup...")
        val authService by inject<IAuthService>()
        repeat(Int.MAX_VALUE) {
            authService.cleanupExpiredSessions()
            delay(SESSION_CLEANUP_DELAY)
        }
    }

fun Application.startRiotPolling() =
    CoroutineScope(Dispatchers.Default).launch {
        logger.debug("Starting Riot polling...")
        val seriesService by inject<ISeriesService>()
        repeat(Int.MAX_VALUE) { tick ->
            try {
                seriesService.pollRiot(includeStale = tick % STALE_POLL_EVERY == 0)
            } catch (e: Exception) {
                logger.warn("Riot poll failed: ${e.message}")
            }
            delay(RIOT_POLL_DELAY)
        }
    }

fun Application.module() {
    logger.info("🔧 Performing opening duties...")

    install(Koin) {
        slf4jLogger()
        modules(
            configModule,
            databaseModule,
            serviceModule,
            repositoryModule,
            gatewayModule,
            httpClientModule,
            hashingModule,
        )
    }

    install(ContentNegotiation) {
        json(
            Json {
                serializersModule =
                    SerializersModule {
                        contextual(Instant::class, InstantSerializer)
                        contextual(UUID::class, UUIDSerializer)
                    }
                encodeDefaults = true
            },
        )
    }
    routes()
    startSessionCleanup()
    // The tournament stub has no games-by-code, so there is nothing to poll.
    if (!get<RiotConfig>().usestubs) startRiotPolling()
    logger.info("🍽️ Denny's is open! Ready to serve requests. 🚀")
}
