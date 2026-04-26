package com.lowbudgetlcs.api.routes.v1.team

import com.lowbudgetlcs.api.dto.teams.NewTeamDto
import com.lowbudgetlcs.api.dto.teams.PatchTeamDto
import com.lowbudgetlcs.api.dto.teams.toDto
import com.lowbudgetlcs.api.dto.teams.toNewTeam
import com.lowbudgetlcs.api.dto.teams.toTeamUpdate
import com.lowbudgetlcs.domain.team.ITeamService
import com.lowbudgetlcs.domain.team.models.toTeamId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.teamRoutesV1() {
    val teamService by inject<ITeamService>()
    route("/team") {
        post<TeamResources> {
            val dto = call.receive<NewTeamDto>()
            logger.debug(dto.toString())
            val created = teamService.createTeam(dto.toNewTeam())
            call.respond(HttpStatusCode.Created, created.toDto())
        }
        get<TeamResources> {
            val teams = teamService.getAllTeams()
            call.respond(teams.map { it.toDto() })
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
    }
}
