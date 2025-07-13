package com.lowbudgetlcs

import com.lowbudgetlcs.routes.routes
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
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

