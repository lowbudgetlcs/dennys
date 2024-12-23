package com.lowbudgetlcs

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.util.logging.*

private val logger = KtorSimpleLogger("com.lowbudgetlcs.App")

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    logger.info("Performing opening duties...")
    // Configure Database
    configureRouting()
    // Start Tournament Engine and Stat Daemons
    TournamentEngine().main()
    logger.info("Denny's is open!")
}
