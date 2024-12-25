package com.lowbudgetlcs

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.Masked
import com.sksamuel.hoplite.addResourceSource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

data class DatabaseConfig(val lblcs: Lblcs)
data class Lblcs(val url: String, val pass: Masked)

class DatabaseBridge {
    private val config =
        ConfigLoaderBuilder.default().addResourceSource("/database.local.yaml", optional = true).build()
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