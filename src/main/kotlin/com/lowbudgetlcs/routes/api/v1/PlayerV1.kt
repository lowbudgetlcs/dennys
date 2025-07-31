package com.lowbudgetlcs.routes.api.v1

import com.lowbudgetlcs.Database
import com.lowbudgetlcs.domain.services.PlayerService
import com.lowbudgetlcs.repositories.jooq.JooqPlayerRepository
import com.lowbudgetlcs.routes.dto.players.NewPlayerDto
import com.lowbudgetlcs.routes.dto.players.toDto
import com.lowbudgetlcs.routes.dto.players.toNewPlayer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)
private val playerService: PlayerService = PlayerService(JooqPlayerRepository(Database.dslContext))

fun Route.playerRoutesV1() {
    route("/player") {
        post {
            logger.info("📩 Received post on /v1/player")
            val newPlayerDto = call.receive<NewPlayerDto>()
            val newPlayer = newPlayerDto.toNewPlayer()

            if (playerService.isNameTaken(newPlayer.name.name)) {
                call.respondText(text = "Player name already exists", status = HttpStatusCode.Conflict)
                return@post
            }

            val created = playerService.createPlayer(newPlayer)
            if (created != null) {
                call.respond(created.toDto())
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

    }
}
