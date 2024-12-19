package com.lowbudgetlcs

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.sksamuel.hoplite.Masked
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

class DatabaseWrapper(private val url: String, private val pass: Masked){
   private val driver by lazy {
       HikariConfig().apply {
           jdbcUrl = url
           password = pass.value
       }.let {
           HikariDataSource(it).asJdbcDriver()
       }
   }

    val db by lazy {
        LblcsDatabase(driver)
    }
}