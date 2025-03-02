package com.lowbudgetlcs

import com.lowbudgetlcs.http.RiotApiClient
import com.lowbudgetlcs.repositories.riot.RiotMatchRepository
import com.lowbudgetlcs.repositories.riot.RiotMatchRepositoryImpl
import com.lowbudgetlcs.util.RateLimiter
import com.lowbudgetlcs.workers.StatDaemon
import com.lowbudgetlcs.workers.TournamentEngine
import io.ktor.server.application.*
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger : Logger = LoggerFactory.getLogger(Application::class.java)
private const val NUM_INSTANCES = 3

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    logger.info("🔧 Performing opening duties...")

    configureRouting()

    val riotMatchRepository = configureRiotMatchRepository()

    // Start Tournament Engine and Stat Daemons- this is a perfect opportunity for a builer/factory pattern.
    logger.info("🏁 Starting background workers...")

    launch {
        logger.info("📊 Launching StatDaemon instances ($NUM_INSTANCES)...")
        StatDaemon.createInstance("STATS").launchInstances(NUM_INSTANCES)
        logger.info("✅ StatDaemon instances are running.")
    }
    launch {
        logger.info("🎮 Launching TournamentEngine instances ($NUM_INSTANCES)...")
        TournamentEngine.createInstance("CALLBACK", riotMatchRepository).launchInstances(NUM_INSTANCES)
        logger.info("✅ TournamentEngine instances are running.")
    }
    logger.info("🍽️ Denny's is open! Ready to serve requests. 🚀")
}

fun Application.configureRiotMatchRepository(): RiotMatchRepository {
    val apiClient = RiotApiClient()
    val rateLimiter = RateLimiter()

    return RiotMatchRepositoryImpl(
        apiClient = apiClient,
        rateLimiter = rateLimiter
    )
}

