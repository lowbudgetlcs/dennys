package com.lowbudgetlcs.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addResourceSource

data class RiotConfig(val apiKey: String, val useStub: Boolean)

object RiotConfigLoader {
    @OptIn(ExperimentalHoplite::class)
    val config: RiotConfig = ConfigLoaderBuilder.default()
        .addResourceSource("/riot.yaml") // Load from riot.yaml
        .withExplicitSealedTypes() // Prevents annoying log message
        .build()
        .loadConfigOrThrow()
}
