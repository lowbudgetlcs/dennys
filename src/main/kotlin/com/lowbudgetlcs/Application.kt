package com.lowbudgetlcs

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.Masked
import com.sksamuel.hoplite.addResourceSource
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.util.logging.*

internal val LOGGER = KtorSimpleLogger("com.lowbudgetlcs.App")

data class Config(val databases: Databases)
data class Databases(val lblcs: DbConfig)
data class DbConfig(val name: String, val url: String, val password: Masked)

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    LOGGER.info("Denny's is officially open to customers!")
    // Configure Database
    val config = ConfigLoaderBuilder.default()
        .addResourceSource("/database.yaml")
        .build()
        .loadConfigOrThrow<Config>()
    val lblcs = Database(config.databases.lblcs).db
    install(ContentNegotiation) {
        json()
    }
    configureRouting()
    // Start Tournament Engine and Stat Daemons
}
