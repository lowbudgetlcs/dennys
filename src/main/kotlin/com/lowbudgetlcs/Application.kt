package com.lowbudgetlcs

import com.lowbudgetlcs.api.dto.InstantSerializer
import com.lowbudgetlcs.api.routes
import com.lowbudgetlcs.di.configModule
import com.lowbudgetlcs.di.dataModule
import com.lowbudgetlcs.di.databaseModule
import com.lowbudgetlcs.di.gatewayModule
import com.lowbudgetlcs.di.httpClientModule
import com.lowbudgetlcs.di.serviceModule
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun main(args: Array<String>) =
    io.ktor.server.netty.EngineMain
        .main(args)

fun Application.module() {
    logger.info("🔧 Performing opening duties...")

    install(Koin) {
        slf4jLogger()
        modules(
            configModule,
            databaseModule,
            serviceModule,
            dataModule,
            gatewayModule,
            httpClientModule
        )
    }

    install(ContentNegotiation) {
        json(
            Json {
                serializersModule =
                    SerializersModule {
                        contextual(Instant::class, InstantSerializer)
                    }
                encodeDefaults = true
            },
        )
    }
    routes()
    logger.info("🍽️ Denny's is open! Ready to serve requests. 🚀")
}
