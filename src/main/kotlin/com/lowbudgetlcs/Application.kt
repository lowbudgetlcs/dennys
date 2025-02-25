package com.lowbudgetlcs

import com.lowbudgetlcs.workers.StatDaemon
import com.lowbudgetlcs.workers.TournamentEngine
import io.ktor.server.application.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch

private val logger = KtorSimpleLogger("com.lowbudgetlcs.Application")

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    logger.info("Performing opening duties...")
    configureRouting()
    // Start Tournament Engine and Stat Daemons- this is a perfect opportunity for a builer/factory pattern.
    launch {
        StatDaemon.createInstance("STATS").launchInstances(3)
    }
    launch {
        TournamentEngine.createInstance("CALLBACK").launchInstances(3)
    }
    logger.info("Denny's is open!")
}

