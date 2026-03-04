package com.lowbudgetlcs.config

import com.sksamuel.hoplite.Masked

data class AppConfig(
    val riot: RiotConfig,
    val database: DatabaseConfig,
    val cookie: CookieConfig,
    val api: ApiConfig,
)

data class CookieConfig(
    val secure: Boolean,
    val expiration: Long,
)

data class RiotConfig(
    val key: String,
    val usestubs: Boolean,
    val providerid: Int,
)

data class DatabaseConfig(
    val url: Masked,
    val password: Masked,
    val logobucketname: String,
)

data class ApiConfig(
  val cors: String? = null
)
