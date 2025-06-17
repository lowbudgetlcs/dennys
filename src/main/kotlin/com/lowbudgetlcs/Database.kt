package com.lowbudgetlcs

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.lowbudgetlcs.config.DatabaseConfig
import com.lowbudgetlcs.config.DatabaseConfigLoader
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.Masked
import com.sksamuel.hoplite.addResourceSource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

private val driver by lazy {
    HikariConfig().apply {
        jdbcUrl = DatabaseConfigLoader.config.url.value
        password = DatabaseConfigLoader.config.pass.value
        maximumPoolSize = 15
        minimumIdle = 1
        idleTimeout = 10000
        connectionTimeout = 30000
    }.let {
        HikariDataSource(it).asJdbcDriver()
    }
}

/**
 * The SqlDelight [Database] object.
 */
fun database() = Database(driver)