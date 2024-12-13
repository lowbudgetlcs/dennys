package com.lowbudgetlcs

import io.ktor.server.application.*

fun main(args: Array<String>) {
    // Main API
    io.ktor.server.netty.EngineMain.main(args)
    // Start Tournament Engine and Stat Daemons
}

fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureRouting()
}
