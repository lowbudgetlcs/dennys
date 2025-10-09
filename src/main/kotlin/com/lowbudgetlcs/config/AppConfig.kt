package com.lowbudgetlcs.config

import com.sksamuel.hoplite.Masked

data class AppConfig(
    val riot: RiotConfig,
    val database: DatabaseConfig,
    val security: SecurityConfig,
)

data class RiotConfig(
    val key: String,
    val usestubs: Boolean,
)

data class DatabaseConfig(
    val url: Masked,
    val password: Masked,
)

data class SecurityConfig(
    val createusersecret: Masked,
    val jwtsecret: Masked,
)
