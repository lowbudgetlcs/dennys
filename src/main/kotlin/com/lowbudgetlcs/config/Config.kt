package com.lowbudgetlcs.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addEnvironmentSource
import com.sksamuel.hoplite.addResourceSource

val appConfig: AppConfig by lazy {
    ConfigLoaderBuilder
        .default()
        .withReport()
        .addEnvironmentSource()
        .addResourceSource("/default.properties")
        .addResourceSource("/secret.properties", optional = true)
        .build()
        .loadConfigOrThrow<AppConfig>()
}
