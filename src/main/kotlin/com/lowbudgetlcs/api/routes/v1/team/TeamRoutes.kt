package com.lowbudgetlcs.api.routes.v1.team

import com.lowbudgetlcs.api.dto.events.EventFilterParams
import com.lowbudgetlcs.api.dto.teams.NewTeamDto
import com.lowbudgetlcs.api.dto.teams.PatchTeamDto
import com.lowbudgetlcs.api.dto.teams.TeamFilterParams
import com.lowbudgetlcs.api.dto.teams.TeamPlayerLinkRequestDto
import com.lowbudgetlcs.api.dto.teams.toDto
import com.lowbudgetlcs.api.dto.teams.toNewTeam
import com.lowbudgetlcs.api.dto.teams.toQuery
import com.lowbudgetlcs.api.dto.teams.toTeamUpdate
import com.lowbudgetlcs.domain.player.models.toPlayerId
import com.lowbudgetlcs.domain.team.ITeamService
import com.lowbudgetlcs.domain.team.models.toTeamId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.teamRoutesV1(teamService: ITeamService) {
    route("/team") {
        get<TeamResources> { route ->
            val filter =
                TeamFilterParams(
                    name = route.name,
                )
            val teams = teamService.getAllTeams(filter.toQuery())
            call.respond(teams.map { it.toDto() })
        }
        post<TeamResources> {
            val dto = call.receive<NewTeamDto>()
            logger.debug(dto.toString())
            val created = teamService.createTeam(dto.toNewTeam())
            call.respond(HttpStatusCode.Created, created.toDto())
        }
        get<TeamResources.ById> { route ->
            val team = teamService.getTeam(route.teamId.toTeamId())
            call.respond(team.toDto())
        }
        patch<TeamResources.ById> { route ->
            val dto = call.receive<PatchTeamDto>()
            val updated = teamService.patchTeam(route.teamId.toTeamId(), dto.toTeamUpdate())
            call.respond(updated.toDto())
        }
        get<TeamResources.ByIdPlayers> { route ->
            val team = teamService.getTeamWithPlayers(route.teamId.toTeamId())
            call.respond(team.toDto())
        }
        post<TeamResources.ByIdPlayers> { route ->
            val dto = call.receive<TeamPlayerLinkRequestDto>()
            logger.debug(dto.toString())
            val teamId = route.teamId.toTeamId()
            val team = teamService.addPlayerToTeam(dto.playerId.toPlayerId(), teamId)
            call.respond(team.toDto())
        }
        delete<TeamResources.ByIdPlayersById> { route ->
            val teamId = route.teamId.toTeamId()
            val playerId = route.playerId.toPlayerId()
            val team = teamService.removePlayerFromTeam(playerId, teamId)
            call.respond(team.toDto())
        }
    }
}
