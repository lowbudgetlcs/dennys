package com.lowbudgetlcs.config

data class AppConfig(
    val riot: RiotConfig
)

data class RiotConfig(val key: String, val useStubs: Boolean)