package com.lowbudgetlcs

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

class LblcsDatabase(private val config: DatabaseConfig = configBinder.bindOrThrow<DatabaseConfig>("database")) {
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
    val db = Database(driver)
}