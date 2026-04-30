package com.lowbudgetlcs

import com.lowbudgetlcs.api.routes
import com.lowbudgetlcs.config.modules.configModule
import com.lowbudgetlcs.config.modules.databaseModule
import com.lowbudgetlcs.config.modules.gatewayModule
import com.lowbudgetlcs.config.modules.hashingModule
import com.lowbudgetlcs.config.modules.httpClientModule
import com.lowbudgetlcs.config.modules.repositoryModule
import com.lowbudgetlcs.config.modules.serviceModule
import com.lowbudgetlcs.domain.auth.IAuthService
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
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

val <T : Any> T.logger: Logger
    get() = LoggerFactory.getLogger(this.javaClass)

private val SESSION_CLEANUP_DELAY = 360.seconds

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
    logger.info("🍽️ Denny's is open! Ready to serve requests. 🚀")
}
