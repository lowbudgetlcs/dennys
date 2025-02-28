package com.lowbudgetlcs

import com.lowbudgetlcs.routes.riot.riotRoutes
import com.lowbudgetlcs.routes.rootRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }
    rootRoutes()
    riotRoutes()
}