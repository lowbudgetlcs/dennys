package com.lowbudgetlcs.routes.api.v1

import com.lowbudgetlcs.domain.models.toPlayerId
import com.lowbudgetlcs.domain.models.toRiotAccountId
import com.lowbudgetlcs.domain.services.PlayerService
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
    playerService: PlayerService
) {
    route("/player") {

        post {
            logger.info("📩 Received post on /v1/player")
            val dto = call.receive<NewPlayerDto>()

            try {
                val created = playerService.createPlayer(dto.toNewPlayer())
                call.respond(created.toDto())
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid input")
            } catch (e: IllegalStateException) {
                call.respond(HttpStatusCode.Conflict, e.message ?: "Conflict")
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
            try {
                val player = playerService.getPlayer(playerId)
                call.respond(player.toDto())
            } catch(e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Not Found")
            }
        }

        patch("{playerId}") {
            logger.info("📩 Received patch on /v1/player/{playerId}")
            val playerId = call.parameters["playerId"]?.toIntOrNull()?.toPlayerId()
                ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid player ID")

            val dto = call.receive<PatchPlayerDto>()

            try {
                val updated = playerService.renamePlayer(playerId, dto.name)
                call.respond(updated.toDto())
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid input")
            } catch (e: IllegalStateException) {
                call.respond(HttpStatusCode.Conflict, e.message ?: "Conflict")
            } catch (e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Not found")
            }
        }

        post("{playerId}/accounts") {
            val playerId = call.parameters["playerId"]?.toIntOrNull()?.toPlayerId()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid player ID")

            val body = call.receive<Map<String, Int>>()
            val accountId = body["accountId"]?.toRiotAccountId()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing accountId")

            try {
                val updated = playerService.linkAccountToPlayer(playerId, accountId)
                call.respond(updated.toDto())
            } catch (e: IllegalStateException) {
                call.respond(HttpStatusCode.Conflict, e.message ?: "Conflict linking account")
            }
        }

        delete("{playerId}/accounts/{accountId}") {
            val playerId = call.parameters["playerId"]?.toIntOrNull()?.toPlayerId()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid player ID")

            val accountId = call.parameters["accountId"]?.toIntOrNull()?.toRiotAccountId()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid account ID")

            try {
                val updated = playerService.unlinkAccountFromPlayer(playerId, accountId)
                call.respond(updated.toDto())
            } catch(e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Not found")
            }
        }

    }
}
