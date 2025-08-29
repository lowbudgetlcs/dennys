package com.lowbudgetlcs.routes.api.v1.player

import com.lowbudgetlcs.domain.models.riot.toRiotAccountId
import com.lowbudgetlcs.domain.models.toPlayerId
import com.lowbudgetlcs.domain.services.PlayerService
import com.lowbudgetlcs.routes.dto.players.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("PlayerEndpointsV1")

fun Route.playerEndpointsV1(
    playerService: PlayerService
) {
    get<PlayerResourcesV1> {
        logger.info("📩 Received GET /v1/player")
        val players = playerService.getAllPlayers()
        call.respond(players.map { it.toDto() })
    }

    post<PlayerResourcesV1> {
        logger.info("📩 Received POST /v1/player")
        val dto = call.receive<NewPlayerDto>()
        logger.debug("Body: {}", dto)
        val created = playerService.createPlayer(dto.toNewPlayer())
        call.respond(HttpStatusCode.Created, created.toDto())
    }
    get<PlayerResourcesV1.ById> { route ->
        logger.info("📩 Received GET /v1/player/${route.playerId}")
        val player = playerService.getPlayer(route.playerId.toPlayerId())
        call.respond(player.toDto())
    }

    patch<PlayerResourcesV1.ById> { route ->
        logger.info("📩 Received PATCH /v1/player/${route.playerId}")
        val dto = call.receive<PatchPlayerDto>()
        logger.debug("Body: {}", dto)
        val updated = playerService.renamePlayer(route.playerId.toPlayerId(), dto.name)
        call.respond(updated.toDto())
    }

    post<PlayerResourcesV1.Accounts> { route ->
        logger.info("📩 Received POST /v1/player/${route.playerId}/accounts")
        val dto = call.receive<AccountLinkRequestDto>()
        logger.debug("Body: {}", dto)
        val updated = playerService.linkAccountToPlayer(
            route.playerId.toPlayerId(), dto.accountId.toRiotAccountId()
        )
        call.respond(updated.toDto())
    }

    delete<PlayerResourcesV1.AccountById> { route ->
        logger.info("📩 Received DELETE /v1/player/${route.playerId}/accounts/${route.accountId}")
        val updated = playerService.unlinkAccountFromPlayer(
            route.playerId.toPlayerId(), route.accountId.toRiotAccountId()
        )
        call.respond(updated.toDto())
    }
}
