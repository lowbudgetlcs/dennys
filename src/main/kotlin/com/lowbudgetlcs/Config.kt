package com.lowbudgetlcs

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.Masked
import com.sksamuel.hoplite.addResourceSource

data class DatabaseConfig(val url: Masked, val password: Masked)
data class RiotConfig(val key: String, val useStubs: Boolean)

val configBinder by lazy {
    ConfigLoaderBuilder.default()
        .addResourceSource("/config.properties")
        .build()
        .configBinder()
}