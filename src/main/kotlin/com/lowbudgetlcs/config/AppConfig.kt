package com.lowbudgetlcs.config

import com.sksamuel.hoplite.Masked

data class AppConfig(
    val riot: RiotConfig,
    val database: DatabaseConfig,
    val cookie: CookieConfig,
    val cors: CorsConfig,
    val logging: LoggingConfig,
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
)

data class CorsConfig(
    val url: String? = null,
    val scheme: String? = null,
)

data class LoggingConfig(
    val cid: String
)
