package com.lowbudgetlcs.config

import com.sksamuel.hoplite.Masked

data class AppConfig(
    val riot: RiotConfig,
    val database: DatabaseConfig
)

data class RiotConfig(val key: String, val usestubs: Boolean)

data class DatabaseConfig(val url: Masked, val password: Masked)
