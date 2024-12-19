package com.lowbudgetlcs

import io.ktor.server.application.*
import io.ktor.util.logging.*

internal val LOGGER = KtorSimpleLogger("com.lowbudgetlcs.App")

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    // Configure Database
    val lblcsUrl = System.getenv("LBLCS_DB_URL") ?: DatabaseProperties().getProperty("LBLCS_DB_URL")
    val lblcsPw = System.getenv("LBLCS_") ?: DatabaseProperties().getProperty("LBLCS_DB_PW")
    val db = DatabaseWrapper(lblcsUrl, lblcsPw).db
    val divisionsQ = db.divisionsQueries
    LOGGER.info(divisionsQ.selectAll().executeAsList().toString())
    configureSerialization()
    configureRouting()
    // Start Tournament Engine and Stat Daemons
}
