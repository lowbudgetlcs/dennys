package com.lowbudgetlcs

import com.lowbudgetlcs.api.dto.InstantSerializer
import com.lowbudgetlcs.api.routes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun main(args: Array<String>) =
    io.ktor.server.netty.EngineMain
        .main(args)

fun Application.module() {
    logger.info("🔧 Performing opening duties...")
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
