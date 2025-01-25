package com.lowbudgetlcs.routes.api

import com.lowbudgetlcs.routes.api.v1.authtest.authTestRoutes
import com.lowbudgetlcs.routes.api.v1.jsontest.jsonTestRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.util.*

val digestFunction = getDigestFunction("SHA-256") { "ktor${it.length}" }
val hashedUserTable = UserHashedTableAuth(
    table = mapOf(
        "ruuffian" to digestFunction("foobar"), "seir" to digestFunction("password")
    ), digester = digestFunction
)

fun Application.apiRoutes() {
    install(Authentication) {
        basic {
            realm = "Auth test"
            validate { credentials ->
                hashedUserTable.authenticate(credentials)
            }
        }
    }
    routing {
        route("/api/v1") {
            authenticate {
                authTestRoutes()
            }
            jsonTestRoutes()
        }
    }
}
