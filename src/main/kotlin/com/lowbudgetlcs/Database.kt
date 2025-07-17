package com.lowbudgetlcs

import com.sksamuel.hoplite.Masked
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

data class DatabaseConfig(val url: Masked, val password: Masked)

object Database {
    private val dbConfig: DatabaseConfig = Config.binder.bindOrThrow<DatabaseConfig>("database")
    private val dataSource: DataSource by lazy {
        val config = HikariConfig().apply {
            jdbcUrl = dbConfig.url.value
            password = dbConfig.password.value
            maximumPoolSize = 15
            minimumIdle = 1
            idleTimeout = 10000
            connectionTimeout = 30000
        }
        HikariDataSource(config)
    }
    val dslContext = DSL.using(dataSource, SQLDialect.POSTGRES)
}