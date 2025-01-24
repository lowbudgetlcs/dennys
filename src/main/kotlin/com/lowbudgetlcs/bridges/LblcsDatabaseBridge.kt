package com.lowbudgetlcs.bridges

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.lowbudgetlcs.Database
import com.lowbudgetlcs.models.RiftSide
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.Masked
import com.sksamuel.hoplite.addResourceSource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import migrations.Team_game_data

data class DatabaseConfig(val lblcs: Lblcs)
data class Lblcs(val url: Masked, val pass: Masked)

class LblcsDatabaseBridge {
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

    val db = Database(driver)
}