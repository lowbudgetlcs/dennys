package com.lowbudgetlcs

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource


class Database(private val dbConfig: DbConfig){
   private val driver by lazy {
       HikariConfig().apply {
           jdbcUrl = dbConfig.url
           password = dbConfig.password.value
       }.let {
           HikariDataSource(it).asJdbcDriver()
       }
   }

    val db by lazy {
        when (dbConfig.name) {
            "lblcs" -> LblcsDatabase(driver)
            else -> throw(Exception("Unrecognized database name ${dbConfig.name}"))
        }
    }
}