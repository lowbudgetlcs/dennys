package com.lowbudgetlcs.bridges

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.lowbudgetlcs.Database
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.Masked
import com.sksamuel.hoplite.addResourceSource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

data class DatabaseConfig(val lblcs: Lblcs)
data class Lblcs(val url: Masked, val pass: Masked)

/**
 * Configures and exposes an instance of [Database].
 */
class LblcsDatabaseBridge {
    /**
     * One-time Hikari configuration.
     */
    companion object {
        @OptIn(ExperimentalHoplite::class)
        private val config =
            ConfigLoaderBuilder.default().withExplicitSealedTypes().addResourceSource("/database.yaml").build()
                .loadConfigOrThrow<DatabaseConfig>()

        private val driver by lazy {
            HikariConfig().apply {
                jdbcUrl = config.lblcs.url.value
                password = config.lblcs.pass.value
                maximumPoolSize = 15
                minimumIdle = 1
                idleTimeout = 10000
                connectionTimeout = 30000
            }.let {
                HikariDataSource(it).asJdbcDriver()
            }
        }
    }

    /**
     * The main SqlDelight [Database] object. All *LBLCS repositories use this object.
     */
    val db = Database(driver)
}