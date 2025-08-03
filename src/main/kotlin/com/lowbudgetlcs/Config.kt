package com.lowbudgetlcs

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource

object Config {
    val binder = ConfigLoaderBuilder.default().addResourceSource("/config.properties").build().configBinder()
}
