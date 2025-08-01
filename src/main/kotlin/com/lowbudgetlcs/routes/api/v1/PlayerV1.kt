package com.lowbudgetlcs.routes.api.v1

import com.lowbudgetlcs.Database
import com.lowbudgetlcs.domain.models.PlayerWithAccounts
import com.lowbudgetlcs.domain.models.toPlayerId
import com.lowbudgetlcs.domain.services.PlayerService
import com.lowbudgetlcs.repositories.jooq.JooqPlayerRepository
import com.lowbudgetlcs.routes.dto.players.NewPlayerDto
import com.lowbudgetlcs.routes.dto.players.PlayerDto
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

            if (newPlayerDto.name.isBlank()) {
                call.respondText(
                    text = "Player name cannot be blank",
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }

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

        get {
            logger.info("📩 Received get on /v1/player")
            val players = playerService.getAllPlayers()
            val playersWithAccountsDto : List<PlayerDto> = players.map { it.toDto() }
            call.respond(playersWithAccountsDto)
        }

        get("{playerId}") {
            logger.info("📩 Received get on /v1/player/{playerId}")
            val playerIdField = call.parameters["playerId"]?.toIntOrNull()
                ?: return@get call.respondText(
                    text = "Invalid player ID",
                    status = HttpStatusCode.BadRequest
                )

            val playerId = playerIdField.toPlayerId()
            val player = playerService.getPlayer(playerId)
            if (player != null) {
                call.respond(player.toDto())
            } else {
                call.respondText("Player not found", status = HttpStatusCode.NotFound)
            }
        }
    }
}
