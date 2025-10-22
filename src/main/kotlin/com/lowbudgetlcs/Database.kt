package com.lowbudgetlcs

import com.lowbudgetlcs.config.DatabaseConfig
import com.lowbudgetlcs.config.appConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

object Database {
    private val dbConfig: DatabaseConfig = appConfig.database
    private val dataSource: DataSource by lazy {
        val config =
            HikariConfig().apply {
                jdbcUrl = dbConfig.url.value
                password = dbConfig.password.value
                maximumPoolSize = 30
                minimumIdle = 5
                idleTimeout = 10000
                connectionTimeout = 30000
            }
        HikariDataSource(config)
    }
    val dslContext = DSL.using(dataSource, SQLDialect.POSTGRES)
}
