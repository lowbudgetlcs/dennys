package com.lowbudgetlcs

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import io.ktor.util.logging.*

class TournamentEngine {
    private val logger = KtorSimpleLogger("com.lowbudgetlcs.TournamentEngine")
    // Configure Database
    private val config =
        ConfigLoaderBuilder.default().addResourceSource("/database.yaml").build().loadConfigOrThrow<Config>()
    private val lblcs = Lblcs(config.lblcs).db
    private val local = Local(config.local).db
    fun main() {
        logger.info("TournamentEngine running...")
        logger.info("Divisions: ${lblcs.divisionsQueries.selectAll().executeAsList()}")
        logger.info("Users: ${local.resultsQueries.selectAll().executeAsList()}")
    }
}