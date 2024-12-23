package com.lowbudgetlcs

import io.ktor.server.application.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val logger = KtorSimpleLogger("com.lowbudgetlcs.Application")

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() = runBlocking {
    logger.info("Performing opening duties...")
    // Configure Database
    configureRouting()
    // Start Tournament Engine and Stat Daemons
    launch {
        TournamentEngine().main()
    }
    logger.info("Denny's is open!")
}
