package com.lowbudgetlcs

import com.lowbudgetlcs.config.AppConfig
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addEnvironmentSource
import com.sksamuel.hoplite.addResourceSource

val appConfig: AppConfig by lazy {
    ConfigLoaderBuilder.default()
        .addResourceSource("/config.properties")
        .addEnvironmentSource()
        .build()
        .loadConfigOrThrow<AppConfig>()
}