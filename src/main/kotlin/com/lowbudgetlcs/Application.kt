package com.lowbudgetlcs

import com.lowbudgetlcs.routes.dto.InstantSerializer
import com.lowbudgetlcs.routes.routes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json(Json {
            serializersModule = SerializersModule {
                contextual(Instant::class, InstantSerializer)
            }
        })
    }
    routes()
}

fun Application.module() {
    logger.info("🔧 Performing opening duties...")
    configureRouting()
    logger.info("🍽️ Denny's is open! Ready to serve requests. 🚀")
}

