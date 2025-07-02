package com.lowbudgetlcs

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.sksamuel.hoplite.Masked
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

data class DatabaseConfig(val url: Masked, val password: Masked)

class Database(private val config: DatabaseConfig = Config.binder.bindOrThrow<DatabaseConfig>("database")) {
    private val driver by lazy {
        HikariConfig().apply {
            jdbcUrl = config.url.value
            password = config.password.value
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
    val db = Dennys(driver)
}