package com.lowbudgetlcs.routes.api.v1

import com.lowbudgetlcs.Database
import com.lowbudgetlcs.domain.models.PlayerWithAccounts
import com.lowbudgetlcs.domain.models.toPlayerId
import com.lowbudgetlcs.domain.services.PlayerAccountService
import com.lowbudgetlcs.domain.services.PlayerService
import com.lowbudgetlcs.gateways.IRiotAccountGateway
import com.lowbudgetlcs.repositories.jooq.JooqPlayerRepository
import com.lowbudgetlcs.routes.dto.players.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.playerRoutesV1(
    playerService: PlayerService,
    playerAccountService: PlayerAccountService,
    riotGateway: IRiotAccountGateway
) {
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

        patch("{playerId}") {
            logger.info("📩 Received patch on /v1/player/{playerId}")
            val playerIdField = call.parameters["playerId"]?.toIntOrNull()
                ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid player ID")

            val dto = call.receive<PatchPlayerDto>()
            if (dto.name.isBlank()) {
                return@patch call.respond(HttpStatusCode.BadRequest, "Player name cannot be blank")
            }

            if (playerService.isNameTaken(dto.name)) {
                return@patch call.respond(HttpStatusCode.Conflict, "Player name already exists")
            }

            val updated = playerService.renamePlayer(playerIdField.toPlayerId(), dto.name)
                ?: return@patch call.respond(HttpStatusCode.NotFound, "Player not found")

            call.respond(updated.toDto())
        }

        post("{playerId}/accounts") {
            logger.info("📩 Received post on /v1/player/{playerId}/accounts")
            val playerIdField = call.parameters["playerId"]?.toIntOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid player ID")

            val playerId = playerIdField.toPlayerId()
            val request = call.receive<AddAccountToPlayerDto>()

            if (request.riotPuuid.isBlank()) {
                return@post call.respond(HttpStatusCode.BadRequest, "PUUID cannot be blank")
            }

            try {
                val updated = playerAccountService.addAccountToPlayer(playerId, request.riotPuuid)
                    ?: return@post call.respond(HttpStatusCode.NotFound, "Player not found")
                call.respond(updated.toDto())
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid input")
            } catch (e: IllegalStateException) {
                call.respond(HttpStatusCode.Conflict, e.message ?: "Conflict")
            } catch (e: Exception) {
                logger.error("❌ Unexpected error", e)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

    }
}
