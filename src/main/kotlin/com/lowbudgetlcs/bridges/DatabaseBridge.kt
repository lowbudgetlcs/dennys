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
data class Lblcs(val url: String, val pass: Masked)

class DatabaseBridge {
    @OptIn(ExperimentalHoplite::class)
    private val config =
        ConfigLoaderBuilder.default().addResourceSource("/database.local.yaml", optional = true).withExplicitSealedTypes().build()
            .loadConfigOrThrow<DatabaseConfig>("/database.yaml")
    private val driver by lazy {
        HikariConfig().apply {
            jdbcUrl = config.lblcs.url
            password = config.lblcs.pass.value
        }.let {
            HikariDataSource(it).asJdbcDriver()
        }
    }
    val db by lazy {
        Database(driver)
    }
}