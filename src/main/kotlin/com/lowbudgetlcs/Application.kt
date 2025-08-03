package com.lowbudgetlcs

import com.lowbudgetlcs.gateways.IRiotAccountGateway
import com.lowbudgetlcs.gateways.RiotAccountGateway
import com.lowbudgetlcs.routes.routes
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }
    routes()
}

fun Application.module() {
    logger.info("🔧 Performing opening duties...")
    configureRouting()
    logger.info("🍽️ Denny's is open! Ready to serve requests. 🚀")
}

