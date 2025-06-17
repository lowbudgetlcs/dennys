package com.lowbudgetlcs

import com.lowbudgetlcs.http.RiotApiClient
import com.lowbudgetlcs.repositories.DatabaseGameRepository
import com.lowbudgetlcs.repositories.DatabasePlayerRepository
import com.lowbudgetlcs.repositories.RiotMatchRepository
import com.lowbudgetlcs.repositories.DatabaseSeriesRepository
import com.lowbudgetlcs.repositories.DatabaseTeamRepository
import com.lowbudgetlcs.util.RateLimiter
import com.lowbudgetlcs.workers.StatDaemon
import com.lowbudgetlcs.workers.TournamentEngine
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger : Logger = LoggerFactory.getLogger(Application::class.java)
private const val STAT_DAEMON_QUEUE = "STATS"
private const val TOURNAMENT_ENGINE_QUEUE = "CALLBACK"

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    logger.info("🔧 Performing opening duties...")

    configureRouting()

    val statDaemon = StatDaemon(
        queue = STAT_DAEMON_QUEUE,
        gamesRepository = DatabaseGameRepository(),
        playersRepository = DatabasePlayerRepository(),
        teamsRepository = DatabaseTeamRepository(),
        matchRepository = RiotMatchRepository(RiotApiClient(), RateLimiter())
    )

    val tournamentEngine = TournamentEngine(
        queue = TOURNAMENT_ENGINE_QUEUE,
        gamesRepository = DatabaseGameRepository(),
        seriesRepository = DatabaseSeriesRepository(),
        playersRepository = DatabasePlayerRepository(),
        matchRepository = RiotMatchRepository(RiotApiClient(), RateLimiter())
    )

    CoroutineScope(Dispatchers.IO).launch {
        logger.info("📊 Launching StatDaemon...")
        statDaemon.start()
    }

    CoroutineScope(Dispatchers.IO).launch {
        logger.info("🎮 Launching TournamentEngine...")
        tournamentEngine.start()
    }

    logger.info("🍽️ Denny's is open! Ready to serve requests. 🚀")
}

