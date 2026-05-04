package com.lowbudgetlcs.domain

import io.ktor.server.routing.Route


interface FeatureRoute {
    fun register(routing: Route)
}
