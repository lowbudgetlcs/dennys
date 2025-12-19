package com.lowbudgetlcs

import com.lowbudgetlcs.config.DatabaseConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

fun createDslContext(dbConfig: DatabaseConfig): DSLContext {
    val dataSource: DataSource by lazy {
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
    return DSL.using(dataSource, SQLDialect.POSTGRES)
}
