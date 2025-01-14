package com.lowbudgetlcs

import io.ktor.server.application.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch

private val logger = KtorSimpleLogger("com.lowbudgetlcs.Application")

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    logger.info("Performing opening duties...")
    configureRouting()
    // Start Tournament Engine and Stat Daemons
    launch {
        TournamentEngine().main()
    }
    launch {
        TournamentEngine().main()
    }
    launch {
        StatDaemon().main()
    }
    launch {
        StatDaemon().main()
    }
    logger.info("Denny's is open!")
}
