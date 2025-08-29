package com.lowbudgetlcs.api.routes.v1.player

import com.lowbudgetlcs.api.dto.players.*
import com.lowbudgetlcs.api.setCidContext
import com.lowbudgetlcs.domain.models.riot.toRiotAccountId
import com.lowbudgetlcs.domain.models.toPlayerId
import com.lowbudgetlcs.domain.services.player.PlayerService
import io.ktor.http.*
import io.ktor.server.application.Application
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.playerEndpointsV1(
    playerService: PlayerService
) {
    get<PlayerResourcesV1> {
        call.setCidContext {
            logger.info("📩 Received GET /v1/player")
            val players = playerService.getAllPlayers()
            call.respond(players.map { it.toDto() })
        }
    }

    post<PlayerResourcesV1> {
        call.setCidContext {
            logger.info("📩 Received POST /v1/player")
            val dto = call.receive<NewPlayerDto>()
            logger.debug(dto.toString())
            val created = playerService.createPlayer(dto.toNewPlayer())
            call.respond(HttpStatusCode.Created, created.toDto())
        }
    }
    get<PlayerResourcesV1.ById> { route ->
        call.setCidContext {
            logger.info("📩 Received GET /v1/player/${route.playerId}")
            val player = playerService.getPlayer(route.playerId.toPlayerId())
            call.respond(player.toDto())
        }
    }

    patch<PlayerResourcesV1.ById> { route ->
        call.setCidContext {
            logger.info("📩 Received PATCH /v1/player/${route.playerId}")
            val dto = call.receive<PatchPlayerDto>()
            logger.debug(dto.toString())
            val updated = playerService.renamePlayer(route.playerId.toPlayerId(), dto.name)
            call.respond(updated.toDto())
        }
    }

    post<PlayerResourcesV1.Accounts> { route ->
        call.setCidContext {
            logger.info("📩 Received POST /v1/player/${route.playerId}/accounts")
            val dto = call.receive<AccountLinkRequestDto>()
            logger.debug(dto.toString())
            val updated = playerService.linkAccountToPlayer(
                route.playerId.toPlayerId(), dto.accountId.toRiotAccountId()
            )
            call.respond(updated.toDto())
        }
    }

    delete<PlayerResourcesV1.AccountById> { route ->
        call.setCidContext {
            logger.info("📩 Received DELETE /v1/player/${route.playerId}/accounts/${route.accountId}")
            val updated = playerService.unlinkAccountFromPlayer(
                route.playerId.toPlayerId(), route.accountId.toRiotAccountId()
            )
            call.respond(updated.toDto())
        }
    }
}
