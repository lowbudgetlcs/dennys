package com.lowbudgetlcs.domain

import io.ktor.server.routing.Route


interface ApiRoute {
    fun register(routing: Route)
}
