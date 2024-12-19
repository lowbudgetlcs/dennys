package com.lowbudgetlcs

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.Masked
import com.sksamuel.hoplite.addResourceSource
import io.ktor.server.application.*
import io.ktor.util.logging.*

internal val LOGGER = KtorSimpleLogger("com.lowbudgetlcs.App")

data class Config(val databases: Databases)
data class Databases(val lblcs: Lblcs)
data class Lblcs(val url: String, val password: Masked)

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    // Configure Database
    val config = ConfigLoaderBuilder.default()
        .addResourceSource("/database.yaml")
        .build()
        .loadConfigOrThrow<Config>()
    val db = DatabaseWrapper(config.databases.lblcs.url, config.databases.lblcs.password).db
    val divisionsQ = db.divisionsQueries
    LOGGER.info(divisionsQ.selectAll().executeAsList().toString())
    configureSerialization()
    configureRouting()
    // Start Tournament Engine and Stat Daemons
}
