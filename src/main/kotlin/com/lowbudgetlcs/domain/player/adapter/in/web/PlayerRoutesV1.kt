package com.lowbudgetlcs.domain.player.adapter.`in`.web

import com.lowbudgetlcs.domain.account.adapter.out.persistence.toAccountId
import com.lowbudgetlcs.domain.player.adapter.`in`.web.dto.AccountLinkRequestDto
import com.lowbudgetlcs.domain.player.adapter.`in`.web.dto.NewPlayerDto
import com.lowbudgetlcs.domain.player.adapter.`in`.web.dto.PatchPlayerDto
import com.lowbudgetlcs.domain.player.adapter.`in`.web.dto.toDto
import com.lowbudgetlcs.domain.player.adapter.`in`.web.dto.toNewPlayer
import com.lowbudgetlcs.domain.player.core.model.types.toPlayerId
import com.lowbudgetlcs.domain.player.core.model.types.toPlayerName
import com.lowbudgetlcs.domain.player.core.port.IPlayerService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles


private val logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
fun Route.playerRoutesV1() {
    val playerService by inject<IPlayerService>()

    route("/player") {
        get<PlayerResourcesV1> {
            val players = playerService.getAllPlayers()
            call.respond(players.map { it.toDto() })
        }

        post<PlayerResourcesV1> {
            val dto = call.receive<NewPlayerDto>()
            logger.debug(dto.toString())
            val created = playerService.createPlayer(dto.toNewPlayer())
            call.respond(HttpStatusCode.Created, created.toDto())
        }
        get<PlayerResourcesV1.ById> { route ->
            val player = playerService.getPlayer(route.playerId.toPlayerId())
            call.respond(player.toDto())
        }

        patch<PlayerResourcesV1.ById> { route ->
            val dto = call.receive<PatchPlayerDto>()
            logger.debug(dto.toString())
            val updated = playerService.renamePlayer(route.playerId.toPlayerId(), dto.name.toPlayerName())
            call.respond(updated.toDto())
        }

        get<PlayerResourcesV1.ByIdTeams> { route ->
            val player = playerService.getPlayerWithTeams(route.playerId.toPlayerId())
            call.respond(player.toDto())
        }

        post<PlayerResourcesV1.Accounts> { route ->
            val dto = call.receive<AccountLinkRequestDto>()
            logger.debug(dto.toString())
            val updated =
                playerService.linkAccountToPlayer(
                    route.playerId.toPlayerId(),
                    dto.accountId.toAccountId(),
                )
            call.respond(updated.toDto())
        }

        delete<PlayerResourcesV1.AccountById> { route ->
            val updated =
                playerService.unlinkAccountFromPlayer(
                    route.playerId.toPlayerId(),
                    route.accountId.toAccountId(),
                )
            call.respond(updated.toDto())
        }
    }
}
