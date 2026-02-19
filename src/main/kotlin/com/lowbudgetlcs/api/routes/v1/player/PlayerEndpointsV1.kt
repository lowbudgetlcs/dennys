package com.lowbudgetlcs.api.routes.v1.player

import com.lowbudgetlcs.api.dto.players.AccountLinkRequestDto
import com.lowbudgetlcs.api.dto.players.NewPlayerDto
import com.lowbudgetlcs.api.dto.players.PatchPlayerDto
import com.lowbudgetlcs.api.dto.players.toDto
import com.lowbudgetlcs.api.dto.players.toNewPlayer
import com.lowbudgetlcs.api.logCall
import com.lowbudgetlcs.api.setCidContext
import com.lowbudgetlcs.domain.account.models.toAccountId
import com.lowbudgetlcs.domain.player.IPlayerService
import com.lowbudgetlcs.domain.player.models.toPlayerId
import com.lowbudgetlcs.domain.player.models.toPlayerName
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.playerEndpointsV1(playerService: IPlayerService) {
    get<PlayerResourcesV1> {
        call.setCidContext {
            logCall(call)
            val players = playerService.getAllPlayers()
            call.respond(players.map { it.toDto() })
        }
    }

    post<PlayerResourcesV1> {
        call.setCidContext {
            logCall(call)
            val dto = call.receive<NewPlayerDto>()
            logger.debug(dto.toString())
            val created = playerService.createPlayer(dto.toNewPlayer())
            call.respond(HttpStatusCode.Created, created.toDto())
        }
    }
    get<PlayerResourcesV1.ById> { route ->
        call.setCidContext {
            logCall(call)
            val player = playerService.getPlayer(route.playerId.toPlayerId())
            call.respond(player.toDto())
        }
    }

    patch<PlayerResourcesV1.ById> { route ->
        call.setCidContext {
            logCall(call)
            val dto = call.receive<PatchPlayerDto>()
            logger.debug(dto.toString())
            val updated = playerService.renamePlayer(route.playerId.toPlayerId(), dto.name.toPlayerName())
            call.respond(updated.toDto())
        }
    }

    post<PlayerResourcesV1.Accounts> { route ->
        call.setCidContext {
            logCall(call)
            val dto = call.receive<AccountLinkRequestDto>()
            logger.debug(dto.toString())
            val updated =
                playerService.linkAccountToPlayer(
                    route.playerId.toPlayerId(),
                    dto.accountId.toAccountId(),
                )
            call.respond(updated.toDto())
        }
    }

    delete<PlayerResourcesV1.AccountById> { route ->
        call.setCidContext {
            logCall(call)
            val updated =
                playerService.unlinkAccountFromPlayer(
                    route.playerId.toPlayerId(),
                    route.accountId.toAccountId(),
                )
            call.respond(updated.toDto())
        }
    }
}
