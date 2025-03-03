package com.lowbudgetlcs.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource

data class RiotConfig(val apiKey: String, val useStub: Boolean)

object RiotConfigLoader {
    val config: RiotConfig = ConfigLoaderBuilder.default()
        .addResourceSource("/riot.yaml") // Load from riot.yaml
        .build()
        .loadConfigOrThrow()
}
