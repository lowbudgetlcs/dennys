package com.lowbudgetlcs

import com.lowbudgetlcs.http.RiotApiClient
import com.lowbudgetlcs.repositories.games.AllGamesDatabase
import com.lowbudgetlcs.repositories.players.AllPlayersDatabase
import com.lowbudgetlcs.repositories.riot.MatchRepositoryRiot
import com.lowbudgetlcs.repositories.teams.AllTeamsDatabase
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
private const val NUM_INSTANCES = 3
private const val STAT_DAEMON_QUEUE = "STATS"
private const val TOURNAMENT_ENGINE_QUEUE = "CALLBACK"

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    logger.info("🔧 Performing opening duties...")

    configureRouting()

    val statDaemon = StatDaemon(
        queue = STAT_DAEMON_QUEUE,
        gamesRepository = AllGamesDatabase(),
        playersRepository = AllPlayersDatabase(),
        teamsRepository = AllTeamsDatabase(),
        matchRepository = MatchRepositoryRiot(RiotApiClient(), RateLimiter())
    )

    CoroutineScope(Dispatchers.IO).launch {
        logger.info("📊 Launching StatDaemon instances")
        statDaemon.start()
    }

    launch {
        logger.info("🎮 Launching TournamentEngine instances ($NUM_INSTANCES)...")
        TournamentEngine.createInstance("CALLBACK").launchInstances(NUM_INSTANCES)
        logger.info("✅ TournamentEngine instances are running.")
    }
    logger.info("🍽️ Denny's is open! Ready to serve requests. 🚀")
}

