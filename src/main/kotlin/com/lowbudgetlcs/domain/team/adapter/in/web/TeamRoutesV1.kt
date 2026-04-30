package com.lowbudgetlcs.domain.team.adapter.`in`.web

import com.lowbudgetlcs.domain.player.core.model.types.toPlayerId
import com.lowbudgetlcs.domain.team.adapter.`in`.web.dto.NewTeamDto
import com.lowbudgetlcs.domain.team.adapter.`in`.web.dto.PatchTeamDto
import com.lowbudgetlcs.domain.team.adapter.`in`.web.dto.TeamFilterParams
import com.lowbudgetlcs.domain.team.adapter.`in`.web.dto.TeamPlayerLinkRequestDto
import com.lowbudgetlcs.domain.team.adapter.`in`.web.dto.toDto
import com.lowbudgetlcs.domain.team.adapter.`in`.web.dto.toNewTeam
import com.lowbudgetlcs.domain.team.adapter.`in`.web.dto.toQuery
import com.lowbudgetlcs.domain.team.adapter.`in`.web.dto.toTeamUpdate
import com.lowbudgetlcs.domain.team.core.model.types.toTeamId
import com.lowbudgetlcs.domain.team.core.port.ITeamService
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
fun Route.teamRoutesV1() {
    val teamService by inject<ITeamService>()

    route("/team") {
        get<TeamResourcesV1> { route ->
            val filter = TeamFilterParams(
                name = route.name,
            )
            val teams = teamService.getAllTeams(filter.toQuery())
            call.respond(teams.map { it.toDto() })
        }
        post<TeamResourcesV1> {
            val dto = call.receive<NewTeamDto>()
            logger.debug(dto.toString())
            val created = teamService.createTeam(dto.toNewTeam())
            call.respond(HttpStatusCode.Created, created.toDto())
        }
        get<TeamResourcesV1.ById> { route ->
            val team = teamService.getTeam(route.teamId.toTeamId())
            call.respond(team.toDto())
        }
        patch<TeamResourcesV1.ById> { route ->
            val dto = call.receive<PatchTeamDto>()
            val updated = teamService.patchTeam(route.teamId.toTeamId(), dto.toTeamUpdate())
            call.respond(updated.toDto())
        }
        get<TeamResourcesV1.ByIdPlayers> { route ->
            val team = teamService.getTeamWithPlayers(route.teamId.toTeamId())
            call.respond(team.toDto())
        }
        post<TeamResourcesV1.ByIdPlayers> { route ->
            val dto = call.receive<TeamPlayerLinkRequestDto>()
            logger.debug(dto.toString())
            val teamId = route.teamId.toTeamId()
            val team = teamService.addPlayerToTeam(dto.playerId.toPlayerId(), teamId)
            call.respond(team.toDto())
        }
        delete<TeamResourcesV1.ByIdPlayersById> { route ->
            val teamId = route.teamId.toTeamId()
            val playerId = route.playerId.toPlayerId()
            val team = teamService.removePlayerFromTeam(playerId, teamId)
            call.respond(team.toDto())
        }
    }
}
