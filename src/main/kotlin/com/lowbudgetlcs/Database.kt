package com.lowbudgetlcs

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.sksamuel.hoplite.Masked
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.util.logging.*

data class Config(val lblcs: LblcsConfig, val local: LocalConfig)
data class LblcsConfig(val url: String, val password: Masked)
data class LocalConfig(val file: String)

class Lblcs(config: LblcsConfig) {
    private val driver by lazy {
        HikariConfig().apply {
            jdbcUrl = config.url
            password = config.password.value
        }.let {
            HikariDataSource(it).asJdbcDriver()
        }
    }
    val db by lazy {
        LblcsDatabase(driver)
    }
}

class Local(config: LocalConfig) {
    private val driver by lazy {
        JdbcSqliteDriver(config.file)
    }

    val db by lazy {
        LocalDatabase(driver)
    }
}