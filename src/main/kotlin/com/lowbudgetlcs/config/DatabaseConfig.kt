package com.lowbudgetlcs.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.Masked
import com.sksamuel.hoplite.addResourceSource

data class DatabaseConfig(val url: Masked, val pass: Masked)

object DatabaseConfigLoader {
    @OptIn(ExperimentalHoplite::class)
    val config: DatabaseConfig =
        ConfigLoaderBuilder.default().addResourceSource("/database.yaml").withExplicitSealedTypes().build()
            .loadConfigOrThrow<DatabaseConfig>()
}